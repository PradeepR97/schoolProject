package com.infiniteVision.schoolProject.modules.payment.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.BusinessException;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.payment.dto.request.CancelPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.CollectPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateInvoiceRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.RefundPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentReceiptResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.StudentFeeDueItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.StudentFeeDuesResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.entity.Invoice;
import com.infiniteVision.schoolProject.modules.payment.entity.Payment;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.payment.mapper.PaymentMapper;
import com.infiniteVision.schoolProject.modules.payment.repository.InvoiceRepository;
import com.infiniteVision.schoolProject.modules.payment.repository.PaymentRepository;
import com.infiniteVision.schoolProject.modules.payment.repository.StudentFeeLedgerRepository;
import com.infiniteVision.schoolProject.modules.payment.service.InvoiceService;
import com.infiniteVision.schoolProject.modules.payment.service.PaymentService;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentDocumentNumberGenerator;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentStatusCalculator;
import com.infiniteVision.schoolProject.modules.payment.validator.PaymentValidator;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipDiscountService;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.repository.StudentRepository;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Collects fee payments and updates ledger, invoice, and student fee status atomically.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final StudentRepository studentRepository;
    private final StudentFeeLedgerRepository studentFeeLedgerRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentValidator paymentValidator;
    private final PaymentMapper paymentMapper;
    private final AuditService auditService;
    private final InvoiceService invoiceService;
    private final ScholarshipDiscountService scholarshipDiscountService;

    /**
     * Record payment, sync ledger and invoice balances, refresh student fees status, write audit rows.
     */
    @Override
    @Transactional
    public PaymentReceiptResponseDTO collectPayment(CollectPaymentRequestDTO request) {
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Optional<Payment> existing = paymentRepository.findActiveWithRelationsByIdempotencyKey(
                    request.getIdempotencyKey().trim());
            if (existing.isPresent()) {
                Payment payment = existing.get();
                log.info("Idempotent replay for key={} paymentId={}", request.getIdempotencyKey(), payment.getId());
                return paymentMapper.toReceiptResponse(
                        payment,
                        payment.getLedger(),
                        payment.getInvoice(),
                        payment.getStudent() != null ? payment.getStudent().getFeesPaymentStatus() : null);
            }
        }

        Student student = studentRepository
                .findByIdAndDeletedFalse(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.STUDENT_NOT_FOUND));

        StudentFeeLedger ledger = studentFeeLedgerRepository
                .findActiveWithRelationsById(request.getLedgerId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.FEE_LEDGER_NOT_FOUND));

        paymentValidator.validateCollectPayment(request, student, ledger);

        Invoice invoice = invoiceRepository
                .findByLedger_IdAndDeletedFalse(ledger.getId())
                .orElse(null);
        if (invoice == null) {
            if (Boolean.TRUE.equals(request.getAutoGenerateInvoice())) {
                invoiceService.generateInvoice(
                        GenerateInvoiceRequestDTO.builder().ledgerId(ledger.getId()).build());
                invoice = invoiceRepository
                        .findByLedger_IdAndDeletedFalse(ledger.getId())
                        .orElseThrow(() -> new BusinessException(
                                MessageConstants.INVOICE_NOT_FOUND, HttpStatus.BAD_REQUEST));
            } else {
                throw new BusinessException(
                        MessageConstants.INVOICE_NOT_FOUND + " — generate invoice first or set autoGenerateInvoice=true",
                        HttpStatus.BAD_REQUEST);
            }
        }

        var beforeLedger = paymentMapper.toLedgerSummary(ledger);
        var beforeInvoice = paymentMapper.toInvoiceSummary(invoice);

        AuthenticatedUser caller = currentUser();
        User collectedBy = userRepository.findByIdAndDeletedFalse(caller.getUserId()).orElse(null);

        String receiptNo = PaymentDocumentNumberGenerator.nextReceiptNumber(paymentRepository.countByDeletedFalse() + 1);
        while (paymentRepository.existsByReceiptNoAndDeletedFalse(receiptNo)) {
            receiptNo = PaymentDocumentNumberGenerator.nextReceiptNumber(
                    paymentRepository.countByDeletedFalse() + 1);
        }

        Payment payment = Payment.builder()
                .receiptNo(receiptNo)
                .student(student)
                .ledger(ledger)
                .invoice(invoice)
                .paymentDate(request.getPaymentDate())
                .amountPaid(request.getAmountPaid())
                .paymentMode(request.getPaymentMode())
                .transactionRef(request.getTransactionRef())
                .chequeNo(request.getChequeNo())
                .chequeDate(request.getChequeDate())
                .bankName(request.getBankName())
                .remarks(request.getRemarks())
                .idempotencyKey(
                        request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()
                                ? request.getIdempotencyKey().trim()
                                : null)
                .status(PaymentRecordStatus.SUCCESS)
                .collectedByUser(collectedBy)
                .deleted(Boolean.FALSE)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        BigDecimal newPaid = ledger.getPaidAmount().add(request.getAmountPaid());
        BigDecimal newBalance = ledger.getNetAmount().subtract(newPaid);
        ledger.setPaidAmount(newPaid);
        ledger.setBalanceAmount(newBalance);
        ledger.setStatus(PaymentStatusCalculator.resolveLedgerStatus(
                ledger.getNetAmount(), newPaid, newBalance, ledger.getDueDate()));

        invoice.setPaidAmount(newPaid);
        invoice.setBalanceAmount(newBalance);
        invoice.setStatus(PaymentStatusCalculator.resolveInvoiceStatus(
                newBalance, newPaid, invoice.getDueDate(), invoice.getStatus()));

        studentFeeLedgerRepository.save(ledger);
        invoiceRepository.save(invoice);

        FeesPaymentStatus studentFeesStatus = refreshStudentFeesPaymentStatus(student);

        PaymentReceiptResponseDTO response =
                paymentMapper.toReceiptResponse(savedPayment, ledger, invoice, studentFeesStatus);

        auditService.logCreate(AuditEntityType.PAYMENT, savedPayment.getId(), response);
        auditService.logUpdate(
                AuditEntityType.FEE_LEDGER, ledger.getId(), beforeLedger, paymentMapper.toLedgerSummary(ledger));
        auditService.logUpdate(
                AuditEntityType.INVOICE, invoice.getId(), beforeInvoice, paymentMapper.toInvoiceSummary(invoice));

        log.info(
                "Payment collected receipt={} studentId={} ledgerId={} amount={} by user id={}",
                receiptNo,
                student.getId(),
                ledger.getId(),
                request.getAmountPaid(),
                caller.getUserId());

        return response;
    }

    /**
     * Paginated list of payment receipts with optional student, ledger, invoice, status, and date filters.
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<PaymentListItemResponseDTO> listPayments(
            Long studentId,
            Long ledgerId,
            Long invoiceId,
            PaymentRecordStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {
        validatePagination(page, size);
        int effectiveSize = size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(page, effectiveSize, Sort.by("paymentDate").descending().and(Sort.by("id").descending()));

        Page<Payment> paymentPage = paymentRepository.findAllFiltered(
                studentId, ledgerId, invoiceId, status, fromDate, toDate, pageable);

        List<PaymentListItemResponseDTO> content =
                paymentPage.getContent().stream().map(paymentMapper::toPaymentListItem).toList();

        return PagedResponseDTO.<PaymentListItemResponseDTO>builder()
                .content(content)
                .page(paymentPage.getNumber())
                .size(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .first(paymentPage.isFirst())
                .last(paymentPage.isLast())
                .build();
    }

    /**
     * Cancel a successful payment; reverses ledger and invoice paid amounts.
     */
    @Override
    @Transactional
    public PaymentReceiptResponseDTO cancelPayment(Long paymentId, CancelPaymentRequestDTO request) {
        return reversePayment(paymentId, "CANCEL", request.getReason());
    }

    /**
     * Refund a successful payment; reverses ledger and invoice paid amounts.
     */
    @Override
    @Transactional
    public PaymentReceiptResponseDTO refundPayment(Long paymentId, RefundPaymentRequestDTO request) {
        return reversePayment(paymentId, "REFUND", request.getReason());
    }

    /**
     * Load ledgers for student with optional academic year filter; attach invoice when present.
     */
    @Override
    @Transactional(readOnly = true)
    public StudentFeeDuesResponseDTO getStudentDues(Long studentId, Long academicYearId) {
        Student student = studentRepository
                .findByIdAndDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.STUDENT_NOT_FOUND));

        Long yearId = academicYearId != null ? academicYearId : student.getAcademicYearId();

        List<StudentFeeLedger> ledgers = yearId != null
                ? studentFeeLedgerRepository.findAllActiveWithFeeHeadByStudentIdAndYear(studentId, yearId)
                : studentFeeLedgerRepository.findAllActiveWithFeeHeadByStudentId(studentId);

        List<StudentFeeDueItemResponseDTO> items = new ArrayList<>();
        BigDecimal totalPendingDiscount = BigDecimal.ZERO;
        for (StudentFeeLedger ledger : ledgers) {
            Invoice invoice = invoiceRepository.findByLedger_IdAndDeletedFalse(ledger.getId()).orElse(null);
            FeeStructure structure = ledger.getFeeStructure();
            BigDecimal pendingDiscount = BigDecimal.ZERO;
            ScholarshipApplicationStatus pendingStatus = null;
            if (structure != null && yearId != null) {
                pendingDiscount = scholarshipDiscountService.resolvePendingDiscount(studentId, yearId, structure);
                pendingStatus = scholarshipDiscountService.resolvePendingScholarshipStatus(
                        studentId, yearId, structure);
                totalPendingDiscount = totalPendingDiscount.add(
                        pendingDiscount != null ? pendingDiscount : BigDecimal.ZERO);
            }
            items.add(paymentMapper.toDueItem(ledger, invoice, pendingDiscount, pendingStatus));
        }

        String studentName = student.getFirstName()
                + (student.getLastName() != null && !student.getLastName().isBlank()
                        ? " " + student.getLastName()
                        : "");

        return StudentFeeDuesResponseDTO.builder()
                .studentId(student.getId())
                .admissionNo(student.getAdmissionNo())
                .studentName(studentName.trim())
                .academicYearId(yearId)
                .feesPaymentStatus(student.getFeesPaymentStatus())
                .totalPendingScholarshipDiscount(totalPendingDiscount)
                .ledgers(items)
                .build();
    }

    /**
     * Load payment receipt with current ledger and invoice balances.
     */
    @Override
    @Transactional(readOnly = true)
    public PaymentReceiptResponseDTO getReceiptById(Long paymentId) {
        Payment payment = paymentRepository
                .findActiveWithRelationsById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.PAYMENT_NOT_FOUND));

        StudentFeeLedger ledger = payment.getLedger();
        Invoice invoice = payment.getInvoice();
        Student student = payment.getStudent();

        return paymentMapper.toReceiptResponse(
                payment, ledger, invoice, student != null ? student.getFeesPaymentStatus() : null);
    }

    private FeesPaymentStatus refreshStudentFeesPaymentStatus(Student student) {
        List<StudentFeeLedger> ledgers =
                studentFeeLedgerRepository.findAllByStudent_IdAndDeletedFalseOrderByDueDateAsc(student.getId());
        List<LedgerStatus> statuses = ledgers.stream().map(StudentFeeLedger::getStatus).toList();
        FeesPaymentStatus feesStatus = PaymentStatusCalculator.resolveStudentFeesStatus(statuses);
        student.setFeesPaymentStatus(feesStatus);
        studentRepository.save(student);
        return feesStatus;
    }

    private PaymentReceiptResponseDTO reversePayment(Long paymentId, String reversalLabel, String reason) {
        Payment payment = paymentRepository
                .findActiveWithRelationsById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.PAYMENT_NOT_FOUND));

        paymentValidator.validatePaymentReversible(payment.getStatus());

        StudentFeeLedger ledger = payment.getLedger();
        Invoice invoice = payment.getInvoice();
        Student student = payment.getStudent();

        var beforeLedger = paymentMapper.toLedgerSummary(ledger);
        var beforeInvoice = paymentMapper.toInvoiceSummary(invoice);
        var beforePayment = paymentMapper.toPaymentListItem(payment);

        BigDecimal newPaid = ledger.getPaidAmount().subtract(payment.getAmountPaid());
        if (newPaid.compareTo(BigDecimal.ZERO) < 0) {
            newPaid = BigDecimal.ZERO;
        }
        BigDecimal newBalance = ledger.getNetAmount().subtract(newPaid);

        ledger.setPaidAmount(newPaid);
        ledger.setBalanceAmount(newBalance);
        ledger.setStatus(PaymentStatusCalculator.resolveLedgerStatus(
                ledger.getNetAmount(), newPaid, newBalance, ledger.getDueDate()));

        invoice.setPaidAmount(newPaid);
        invoice.setBalanceAmount(newBalance);
        invoice.setStatus(PaymentStatusCalculator.resolveInvoiceStatus(
                newBalance, newPaid, invoice.getDueDate(), invoice.getStatus()));

        payment.setStatus(
                "REFUND".equals(reversalLabel) ? PaymentRecordStatus.REFUNDED : PaymentRecordStatus.CANCELLED);
        String existingRemarks = payment.getRemarks();
        payment.setRemarks((existingRemarks != null && !existingRemarks.isBlank() ? existingRemarks + " | " : "")
                + reversalLabel + ": " + reason);

        studentFeeLedgerRepository.save(ledger);
        invoiceRepository.save(invoice);
        Payment savedPayment = paymentRepository.save(payment);

        FeesPaymentStatus studentFeesStatus = refreshStudentFeesPaymentStatus(student);

        PaymentReceiptResponseDTO response =
                paymentMapper.toReceiptResponse(savedPayment, ledger, invoice, studentFeesStatus);

        auditService.logUpdate(
                AuditEntityType.PAYMENT, savedPayment.getId(), beforePayment, paymentMapper.toPaymentListItem(savedPayment));
        auditService.logUpdate(
                AuditEntityType.FEE_LEDGER, ledger.getId(), beforeLedger, paymentMapper.toLedgerSummary(ledger));
        auditService.logUpdate(
                AuditEntityType.INVOICE, invoice.getId(), beforeInvoice, paymentMapper.toInvoiceSummary(invoice));

        log.info(
                "{} payment id={} receipt={} studentId={} amount={}",
                reversalLabel,
                paymentId,
                payment.getReceiptNo(),
                student.getId(),
                payment.getAmountPaid());

        return response;
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of("Page index must be zero or greater"));
        }
        if (size < 0) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of("Page size must be zero or greater"));
        }
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
