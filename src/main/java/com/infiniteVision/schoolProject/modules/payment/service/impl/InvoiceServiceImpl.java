package com.infiniteVision.schoolProject.modules.payment.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.BusinessException;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.SectionMasterRepository;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeType;
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateInvoiceRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.entity.Invoice;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import com.infiniteVision.schoolProject.modules.payment.mapper.PaymentMapper;
import com.infiniteVision.schoolProject.modules.payment.repository.InvoiceRepository;
import com.infiniteVision.schoolProject.modules.payment.repository.StudentFeeLedgerRepository;
import com.infiniteVision.schoolProject.modules.payment.service.InvoiceService;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentDocumentNumberGenerator;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
 * Creates {@link Invoice} rows (one per ledger) with fee line breakdown from fee head codes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final StudentFeeLedgerRepository studentFeeLedgerRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClassMasterRepository classMasterRepository;
    private final SectionMasterRepository sectionMasterRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final AuditService auditService;

    /**
     * Build invoice from ledger amounts; assign unique invoice number and {@code generated_by}.
     */
    @Override
    @Transactional
    public InvoiceSummaryResponseDTO generateInvoice(GenerateInvoiceRequestDTO request) {
        StudentFeeLedger ledger = studentFeeLedgerRepository
                .findActiveWithRelationsById(request.getLedgerId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.FEE_LEDGER_NOT_FOUND));

        if (invoiceRepository.findByLedger_IdAndDeletedFalse(ledger.getId()).isPresent()) {
            throw new BusinessException(MessageConstants.INVOICE_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        Student student = ledger.getStudent();
        ClassMaster classMaster = classMasterRepository
                .findByIdAndDeletedFalse(student.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.CLASS_NOT_FOUND));
        SectionMaster section = sectionMasterRepository
                .findByIdAndDeletedFalse(student.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.SECTION_NOT_FOUND));

        AuthenticatedUser caller = currentUser();
        User generatedBy = userRepository
                .findByIdAndDeletedFalse(caller.getUserId())
                .orElse(null);

        LocalDate today = LocalDate.now();
        String invoiceNo = PaymentDocumentNumberGenerator.nextInvoiceNumber(invoiceRepository.count() + 1);

        Invoice invoice = Invoice.builder()
                .invoiceNo(invoiceNo)
                .invoiceDate(today)
                .dueDate(ledger.getDueDate())
                .student(student)
                .classMaster(classMaster)
                .section(section)
                .academicYear(ledger.getAcademicYear())
                .ledger(ledger)
                .term(ledger.getTerm())
                .tuitionFee(BigDecimal.ZERO)
                .examFee(BigDecimal.ZERO)
                .labFee(BigDecimal.ZERO)
                .libraryFee(BigDecimal.ZERO)
                .sportsFee(BigDecimal.ZERO)
                .transportFee(BigDecimal.ZERO)
                .uniformFee(BigDecimal.ZERO)
                .miscFee(BigDecimal.ZERO)
                .grossAmount(BigDecimal.ZERO)
                .discountAmount(ledger.getDiscountAmount())
                .lateFee(ledger.getLateFee())
                .netAmount(ledger.getNetAmount())
                .paidAmount(ledger.getPaidAmount())
                .balanceAmount(ledger.getBalanceAmount())
                .status(paymentMapper.defaultNewInvoiceStatus())
                .generatedByUser(generatedBy)
                .deleted(Boolean.FALSE)
                .build();

        FeeType feeType = ledger.getFeeStructure().getFeeType();
        if (feeType != null) {
            paymentMapper.applyFeeTypeToInvoiceLine(invoice, feeType.getFeeTypeCode(), ledger.getNetAmount());
            paymentMapper.recalculateInvoiceTotals(invoice);
            invoice.setNetAmount(ledger.getNetAmount());
            invoice.setBalanceAmount(ledger.getBalanceAmount());
        }

        Invoice saved = invoiceRepository.save(invoice);
        InvoiceSummaryResponseDTO response = paymentMapper.toInvoiceSummary(saved);
        auditService.logCreate(AuditEntityType.INVOICE, saved.getId(), response);

        log.info("Invoice generated id={} ledgerId={} by user id={}", saved.getId(), ledger.getId(), caller.getUserId());
        return response;
    }

    /**
     * Paginated invoice list with optional student, academic year, and status filters.
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<InvoiceListItemResponseDTO> listInvoices(
            Long studentId, Long academicYearId, InvoiceStatus status, int page, int size) {
        validatePagination(page, size);
        int effectiveSize = size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(page, effectiveSize, Sort.by("invoiceDate").descending().and(Sort.by("id").descending()));

        Page<Invoice> invoicePage =
                invoiceRepository.findAllFiltered(studentId, academicYearId, status, pageable);

        List<InvoiceListItemResponseDTO> content =
                invoicePage.getContent().stream().map(paymentMapper::toInvoiceListItem).toList();

        return PagedResponseDTO.<InvoiceListItemResponseDTO>builder()
                .content(content)
                .page(invoicePage.getNumber())
                .size(invoicePage.getSize())
                .totalElements(invoicePage.getTotalElements())
                .totalPages(invoicePage.getTotalPages())
                .first(invoicePage.isFirst())
                .last(invoicePage.isLast())
                .build();
    }

    /**
     * Load full invoice detail by id.
     */
    @Override
    @Transactional(readOnly = true)
    public InvoiceDetailResponseDTO getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository
                .findActiveWithRelationsById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.INVOICE_NOT_FOUND));
        return paymentMapper.toInvoiceDetail(invoice);
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
