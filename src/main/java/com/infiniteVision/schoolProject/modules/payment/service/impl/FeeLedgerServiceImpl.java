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
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateLedgerRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.GenerateLedgerResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.mapper.PaymentMapper;
import com.infiniteVision.schoolProject.modules.payment.repository.StudentFeeLedgerRepository;
import com.infiniteVision.schoolProject.modules.payment.service.FeeLedgerService;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentTermMapper;
import com.infiniteVision.schoolProject.modules.payment.validator.PaymentValidator;
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

        List<FeeStructure> structures = feeStructureRepository.findActiveByClassIdAndAcademicYearId(
                student.getClassId(), request.getAcademicYearId());

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
}
