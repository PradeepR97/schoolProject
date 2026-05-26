package com.infiniteVision.schoolProject.modules.payment.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeStructureRepository;
import com.infiniteVision.schoolProject.modules.payment.dto.request.AdjustLateFeeRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateLedgerRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.RegenerateLedgerRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.GenerateLedgerResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.RegenerateLedgerResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.mapper.PaymentMapper;
import com.infiniteVision.schoolProject.modules.payment.repository.StudentFeeLedgerRepository;
import com.infiniteVision.schoolProject.modules.payment.service.FeeLedgerService;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentInvoiceSyncHelper;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentStatusCalculator;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentTermMapper;
import com.infiniteVision.schoolProject.modules.payment.validator.PaymentValidator;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipDiscountService;
import com.infiniteVision.schoolProject.modules.scholarship.util.ScholarshipDiscountCalculator;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.repository.StudentRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generates {@link StudentFeeLedger} rows from active {@link FeeStructure} definitions for a student's class.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeeLedgerServiceImpl implements FeeLedgerService {

    private final StudentRepository studentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final StudentFeeLedgerRepository studentFeeLedgerRepository;
    private final PaymentValidator paymentValidator;
    private final PaymentMapper paymentMapper;
    private final AuditService auditService;
    private final ScholarshipDiscountService scholarshipDiscountService;
    private final PaymentInvoiceSyncHelper paymentInvoiceSyncHelper;

    /**
     * For each matching fee structure, create a ledger unless one already exists for (student, year, term, structure).
     */
    @Override
    @Transactional
    public GenerateLedgerResponseDTO generateLedgers(GenerateLedgerRequestDTO request) {
        Student student = studentRepository
                .findByIdAndDeletedFalse(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.STUDENT_NOT_FOUND));

        paymentValidator.validateStudentHasClassAndYear(student);
        paymentValidator.validateStudentYearMatches(student, request.getAcademicYearId());

        academicYearRepository
                .findByIdAndDeletedFalse(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.ACADEMIC_YEAR_NOT_FOUND));

        List<FeeStructure> structures = feeStructureRepository.findActiveByClassIdAndSectionIdAndAcademicYearId(
                student.getClassId(), student.getSectionId(), request.getAcademicYearId());

        if (structures.isEmpty()) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.NO_FEE_STRUCTURES_FOR_CLASS));
        }

        AcademicYear academicYear = academicYearRepository
                .findByIdAndDeletedFalse(request.getAcademicYearId())
                .orElseThrow();

        int created = 0;
        int skipped = 0;
        List<FeeLedgerSummaryResponseDTO> summaries = new ArrayList<>();

        for (FeeStructure structure : structures) {
            if (!Boolean.TRUE.equals(student.getTransportRequired())
                    && structure.getFeeType() != null
                    && ScholarshipDiscountCalculator.isTransportType(structure.getFeeType())) {
                skipped++;
                continue;
            }

            FeeBillingTerm billingTerm = PaymentTermMapper.toBillingTerm(structure.getTermType());
            if (request.getTerm() != null && !request.getTerm().equals(billingTerm)) {
                continue;
            }

            if (studentFeeLedgerRepository.existsByStudent_IdAndAcademicYear_IdAndTermAndFeeStructure_IdAndDeletedFalse(
                    student.getId(), request.getAcademicYearId(), billingTerm, structure.getId())) {
                skipped++;
                continue;
            }

            BigDecimal amount = structure.getAmount();
            StudentFeeLedger ledger = StudentFeeLedger.builder()
                    .student(student)
                    .feeStructure(structure)
                    .academicYear(academicYear)
                    .term(billingTerm)
                    .actualAmount(amount)
                    .discountAmount(BigDecimal.ZERO)
                    .lateFee(BigDecimal.ZERO)
                    .netAmount(amount)
                    .paidAmount(BigDecimal.ZERO)
                    .balanceAmount(amount)
                    .dueDate(structure.getDueDate() != null ? structure.getDueDate() : java.time.LocalDate.now())
                    .status(paymentMapper.defaultNewLedgerStatus())
                    .deleted(Boolean.FALSE)
                    .build();

            scholarshipDiscountService.applyDiscountToLedger(ledger, structure);

            StudentFeeLedger saved = studentFeeLedgerRepository.save(ledger);
            auditService.logCreate(AuditEntityType.FEE_LEDGER, saved.getId(), paymentMapper.toLedgerSummary(saved));
            summaries.add(paymentMapper.toLedgerSummary(saved));
            created++;
        }

        log.info(
                "Fee ledgers generated studentId={} yearId={} created={} skipped={}",
                student.getId(),
                request.getAcademicYearId(),
                created,
                skipped);

        return GenerateLedgerResponseDTO.builder()
                .studentId(student.getId())
                .academicYearId(request.getAcademicYearId())
                .createdCount(created)
                .skippedCount(skipped)
                .ledgers(summaries)
                .build();
    }

    /**
     * Recalculates unpaid ledgers from current active fee structure amounts and approved scholarships.
     */
    @Override
    @Transactional
    public RegenerateLedgerResponseDTO regenerateLedgers(RegenerateLedgerRequestDTO request) {
        Student student = studentRepository
                .findByIdAndDeletedFalse(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.STUDENT_NOT_FOUND));

        paymentValidator.validateStudentHasClassAndYear(student);
        paymentValidator.validateStudentYearMatches(student, request.getAcademicYearId());

        academicYearRepository
                .findByIdAndDeletedFalse(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.ACADEMIC_YEAR_NOT_FOUND));

        List<StudentFeeLedger> ledgers =
                studentFeeLedgerRepository.findAllActiveWithFeeTypeByStudentIdAndYear(
                        student.getId(), request.getAcademicYearId());

        int updated = 0;
        int skipped = 0;
        List<FeeLedgerSummaryResponseDTO> summaries = new ArrayList<>();

        for (StudentFeeLedger ledger : ledgers) {
            if (request.getTerm() != null && !request.getTerm().equals(ledger.getTerm())) {
                continue;
            }
            if (request.getStructureId() != null
                    && (ledger.getFeeStructure() == null
                            || !request.getStructureId().equals(ledger.getFeeStructure().getId()))) {
                continue;
            }
            if (LedgerStatus.PAID.equals(ledger.getStatus())
                    || ledger.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
                skipped++;
                continue;
            }

            FeeStructure structure = ledger.getFeeStructure();
            if (structure == null || Boolean.FALSE.equals(structure.getActive()) || Boolean.TRUE.equals(structure.getDeleted())) {
                skipped++;
                continue;
            }

            var before = paymentMapper.toLedgerSummary(ledger);
            if (structure.getDueDate() != null) {
                ledger.setDueDate(structure.getDueDate());
            }
            scholarshipDiscountService.applyDiscountToLedger(ledger, structure);
            StudentFeeLedger saved = studentFeeLedgerRepository.save(ledger);
            paymentInvoiceSyncHelper.syncInvoiceFromLedger(saved);

            auditService.logUpdate(
                    AuditEntityType.FEE_LEDGER, saved.getId(), before, paymentMapper.toLedgerSummary(saved));
            summaries.add(paymentMapper.toLedgerSummary(saved));
            updated++;
        }

        log.info(
                "Fee ledgers regenerated studentId={} yearId={} updated={} skipped={}",
                student.getId(),
                request.getAcademicYearId(),
                updated,
                skipped);

        return RegenerateLedgerResponseDTO.builder()
                .studentId(student.getId())
                .academicYearId(request.getAcademicYearId())
                .updatedCount(updated)
                .skippedCount(skipped)
                .ledgers(summaries)
                .build();
    }

    /**
     * Sets late fee on an unpaid ledger, recalculates net/balance, and syncs linked invoice.
     */
    @Override
    @Transactional
    public FeeLedgerSummaryResponseDTO adjustLateFee(Long ledgerId, AdjustLateFeeRequestDTO request) {
        StudentFeeLedger ledger = studentFeeLedgerRepository
                .findActiveWithRelationsById(ledgerId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.FEE_LEDGER_NOT_FOUND));

        paymentValidator.validateLateFeeAdjustment(ledger, request.getLateFee());

        var before = paymentMapper.toLedgerSummary(ledger);
        ledger.setLateFee(request.getLateFee());

        FeeStructure structure = ledger.getFeeStructure();
        if (structure != null) {
            scholarshipDiscountService.applyDiscountToLedger(ledger, structure);
        } else {
            BigDecimal net = ledger.getActualAmount()
                    .subtract(ledger.getDiscountAmount() != null ? ledger.getDiscountAmount() : BigDecimal.ZERO)
                    .add(request.getLateFee());
            BigDecimal paid = ledger.getPaidAmount() != null ? ledger.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal balance = net.subtract(paid);
            ledger.setNetAmount(net);
            ledger.setBalanceAmount(balance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : balance);
            ledger.setStatus(PaymentStatusCalculator.resolveLedgerStatus(
                    net, paid, ledger.getBalanceAmount(), ledger.getDueDate()));
        }

        StudentFeeLedger saved = studentFeeLedgerRepository.save(ledger);
        paymentInvoiceSyncHelper.syncInvoiceFromLedger(saved);

        FeeLedgerSummaryResponseDTO after = paymentMapper.toLedgerSummary(saved);
        auditService.logUpdate(AuditEntityType.FEE_LEDGER, saved.getId(), before, after);

        log.info("Late fee adjusted ledgerId={} lateFee={}", ledgerId, request.getLateFee());
        return after;
    }

    /**
     * Loads ledger detail including pending scholarship discount visibility.
     */
    @Override
    @Transactional(readOnly = true)
    public FeeLedgerDetailResponseDTO getLedgerById(Long ledgerId) {
        StudentFeeLedger ledger = studentFeeLedgerRepository
                .findActiveWithRelationsById(ledgerId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.FEE_LEDGER_NOT_FOUND));

        FeeStructure structure = ledger.getFeeStructure();
        BigDecimal pendingDiscount = BigDecimal.ZERO;
        com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus pendingStatus = null;
        if (structure != null && ledger.getStudent() != null && ledger.getAcademicYear() != null) {
            Long studentId = ledger.getStudent().getId();
            Long yearId = ledger.getAcademicYear().getId();
            pendingDiscount = scholarshipDiscountService.resolvePendingDiscount(studentId, yearId, structure);
            pendingStatus = scholarshipDiscountService.resolvePendingScholarshipStatus(studentId, yearId, structure);
        }
        return paymentMapper.toLedgerDetail(ledger, pendingDiscount, pendingStatus);
    }
}
