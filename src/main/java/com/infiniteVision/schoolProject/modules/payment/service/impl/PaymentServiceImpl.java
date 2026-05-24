package com.infiniteVision.schoolProject.modules.payment.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.BusinessException;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.payment.dto.request.CollectPaymentRequestDTO;
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
import com.infiniteVision.schoolProject.modules.payment.service.PaymentService;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentDocumentNumberGenerator;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentStatusCalculator;
import com.infiniteVision.schoolProject.modules.payment.validator.PaymentValidator;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.repository.StudentRepository;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final StudentRepository studentRepository;
    private final StudentFeeLedgerRepository studentFeeLedgerRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentValidator paymentValidator;
    private final PaymentMapper paymentMapper;
    private final AuditService auditService;

    /**
     * Record payment, sync ledger and invoice balances, refresh student fees status, write audit rows.
     */
    @Override
    @Transactional
    public PaymentReceiptResponseDTO collectPayment(CollectPaymentRequestDTO request) {
        Student student = studentRepository
                .findByIdAndDeletedFalse(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.STUDENT_NOT_FOUND));

        StudentFeeLedger ledger = studentFeeLedgerRepository
                .findActiveWithRelationsById(request.getLedgerId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.FEE_LEDGER_NOT_FOUND));

        paymentValidator.validateCollectPayment(request, student, ledger);

        Invoice invoice = invoiceRepository
                .findByLedger_IdAndDeletedFalse(ledger.getId())
                .orElseThrow(() -> new BusinessException(
                        MessageConstants.INVOICE_NOT_FOUND + " — generate invoice first", HttpStatus.BAD_REQUEST));

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
        for (StudentFeeLedger ledger : ledgers) {
            Invoice invoice = invoiceRepository.findByLedger_IdAndDeletedFalse(ledger.getId()).orElse(null);
            items.add(paymentMapper.toDueItem(ledger, invoice));
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

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
