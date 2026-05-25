package com.infiniteVision.schoolProject.modules.payment.receipt.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.BusinessException;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.modules.payment.entity.Payment;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.payment.receipt.config.ReceiptProperties;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.request.PrintReceiptRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptFeeLineResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptPrintHistoryItemDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptPrintResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.entity.ReceiptPrintLog;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrintAction;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrinterType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.ReceiptCopyType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.RenderedFormat;
import com.infiniteVision.schoolProject.modules.payment.receipt.model.ReceiptFeeLine;
import com.infiniteVision.schoolProject.modules.payment.receipt.model.ReceiptPrintModel;
import com.infiniteVision.schoolProject.modules.payment.receipt.repository.ReceiptPrintLogRepository;
import com.infiniteVision.schoolProject.modules.payment.receipt.service.ReceiptDataAggregator;
import com.infiniteVision.schoolProject.modules.payment.receipt.service.ReceiptPdfGenerator;
import com.infiniteVision.schoolProject.modules.payment.receipt.service.ReceiptPrintService;
import com.infiniteVision.schoolProject.modules.payment.receipt.service.ReceiptTextRenderer;
import com.infiniteVision.schoolProject.modules.payment.repository.PaymentRepository;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptPrintServiceImpl implements ReceiptPrintService {

    private final PaymentRepository paymentRepository;
    private final ReceiptPrintLogRepository receiptPrintLogRepository;
    private final ReceiptDataAggregator receiptDataAggregator;
    private final ReceiptTextRenderer receiptTextRenderer;
    private final ReceiptPdfGenerator receiptPdfGenerator;
    private final ReceiptProperties receiptProperties;
    private final AuditService auditService;

    /**
     * Load full receipt detail for preview UI including print history summary.
     */
    @Override
    @Transactional(readOnly = true)
    public ReceiptDetailResponseDTO getReceiptDetails(Long paymentId) {
        Payment payment = loadPayment(paymentId);
        ReceiptPrintModel model = receiptDataAggregator.build(payment, ReceiptCopyType.ORIGINAL, false, 0);
        long printCount = receiptPrintLogRepository.countByPaymentId(paymentId);
        long reprintCount =
                receiptPrintLogRepository.countByPaymentIdAndPrintAction(paymentId, PrintAction.REPRINT);
        return toDetailDto(model, printCount, reprintCount);
    }

    /**
     * First print: render PDF or plain text, persist print log, audit the action.
     */
    @Override
    @Transactional
    public ReceiptPrintResponseDTO printReceipt(Long paymentId, PrintReceiptRequestDTO request) {
        return executePrint(paymentId, request, PrintAction.PRINT, false);
    }

    /**
     * Reprint with watermark and max-reprint guard; increments print sequence.
     */
    @Override
    @Transactional
    public ReceiptPrintResponseDTO reprintReceipt(Long paymentId, PrintReceiptRequestDTO request) {
        if (receiptPrintLogRepository.countByPaymentId(paymentId) == 0) {
            throw new BusinessException(MessageConstants.RECEIPT_REPRINT_REQUIRES_INITIAL_PRINT);
        }
        long reprintCount =
                receiptPrintLogRepository.countByPaymentIdAndPrintAction(paymentId, PrintAction.REPRINT);
        if (reprintCount >= receiptProperties.getMaxReprints()) {
            throw new BusinessException(MessageConstants.RECEIPT_REPRINT_LIMIT_EXCEEDED);
        }
        return executePrint(paymentId, request, PrintAction.REPRINT, true);
    }

    /**
     * Stream the latest or freshly generated laser PDF for download.
     */
    @Override
    @Transactional
    public Resource downloadPdf(Long paymentId, PrinterType printerType, ReceiptCopyType copyType) {
        Payment payment = loadPayment(paymentId);
        int sequence = nextPrintSequence(paymentId);
        ReceiptPrintModel model = receiptDataAggregator.build(
                payment, copyType != null ? copyType : ReceiptCopyType.ORIGINAL, sequence > 1, sequence);
        try {
            Path pdfPath = resolvePdfPath(paymentId, sequence);
            receiptPdfGenerator.generateAndSave(model, pdfPath);
            return new FileSystemResource(pdfPath);
        } catch (Exception e) {
            log.error("PDF download failed paymentId={}", paymentId, e);
            throw new BusinessException(MessageConstants.RECEIPT_PDF_GENERATION_FAILED);
        }
    }

    /**
     * List all print/reprint audit rows for a payment newest first.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReceiptPrintHistoryItemDTO> getPrintHistory(Long paymentId) {
        loadPayment(paymentId);
        return receiptPrintLogRepository.findAllByPaymentIdOrderByPrintedAtDesc(paymentId).stream()
                .map(this::toHistoryItem)
                .toList();
    }

    private ReceiptPrintResponseDTO executePrint(
            Long paymentId,
            PrintReceiptRequestDTO request,
            PrintAction action,
            boolean forceReprint) {
        Payment payment = loadPayment(paymentId);
        if (payment.getStatus() != PaymentRecordStatus.SUCCESS) {
            throw new BusinessException(MessageConstants.RECEIPT_PRINT_ONLY_SUCCESS_PAYMENTS);
        }

        int sequence = nextPrintSequence(paymentId);
        boolean reprint = forceReprint || action == PrintAction.REPRINT || sequence > 1;
        ReceiptPrintModel model = receiptDataAggregator.build(payment, request.getCopyType(), reprint, sequence);

        String username = currentUser().getUsername();
        RenderedFormat format;
        String plainText = null;
        String pdfPath = null;
        String pdfFileName = null;

        PrinterType printerType = request.getPrinterType();
        if (printerType == PrinterType.LASER) {
            format = RenderedFormat.PDF;
            try {
                Path path = resolvePdfPath(paymentId, sequence);
                receiptPdfGenerator.generateAndSave(model, path);
                pdfPath = path.toString();
                pdfFileName = path.getFileName().toString();
            } catch (Exception e) {
                log.error("Receipt PDF generation failed paymentId={}", paymentId, e);
                throw new BusinessException(MessageConstants.RECEIPT_PDF_GENERATION_FAILED);
            }
        } else {
            format = RenderedFormat.PLAIN_TEXT;
            plainText = receiptTextRenderer.render(model, printerType);
        }

        ReceiptPrintLog logEntry = ReceiptPrintLog.builder()
                .paymentId(paymentId)
                .receiptNo(payment.getReceiptNo())
                .printerType(printerType)
                .copyType(request.getCopyType())
                .printAction(action)
                .printCount(sequence)
                .pdfPath(pdfPath)
                .renderedFormat(format)
                .watermarkApplied(reprint)
                .printedBy(username)
                .printedAt(LocalDateTime.now())
                .remarks(request.getRemarks())
                .build();
        receiptPrintLogRepository.save(logEntry);

        Map<String, Object> auditPayload = new HashMap<>();
        auditPayload.put("paymentId", paymentId);
        auditPayload.put("receiptNo", payment.getReceiptNo());
        auditPayload.put("printerType", printerType.name());
        auditPayload.put("action", action.name());
        auditPayload.put("sequence", sequence);
        auditService.logCreate(AuditEntityType.PAYMENT, paymentId, auditPayload);

        log.info(
                "Receipt {} paymentId={} seq={} printer={} by {}",
                action.name(),
                paymentId,
                sequence,
                printerType,
                username);

        String downloadPath = PaymentApiPaths.receiptPdf(paymentId);
        return ReceiptPrintResponseDTO.builder()
                .paymentId(paymentId)
                .receiptNo(payment.getReceiptNo())
                .printerType(printerType)
                .copyType(request.getCopyType())
                .printAction(action)
                .renderedFormat(format)
                .plainTextContent(plainText)
                .pdfFileName(pdfFileName)
                .pdfDownloadPath(downloadPath)
                .printSequence(sequence)
                .watermarkApplied(reprint)
                .printedAt(logEntry.getPrintedAt())
                .build();
    }

    private Payment loadPayment(Long paymentId) {
        return paymentRepository
                .findActiveWithRelationsById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.PAYMENT_NOT_FOUND));
    }

    private int nextPrintSequence(Long paymentId) {
        return (int) receiptPrintLogRepository.countByPaymentId(paymentId) + 1;
    }

    private Path resolvePdfPath(Long paymentId, int sequence) {
        String fileName = "receipt-" + paymentId + "-" + sequence + ".pdf";
        return Path.of(receiptProperties.getStoragePath()).resolve(fileName);
    }

    private ReceiptDetailResponseDTO toDetailDto(ReceiptPrintModel model, long printCount, long reprintCount) {
        List<ReceiptFeeLineResponseDTO> lines = model.getFeeLines().stream()
                .map(l -> ReceiptFeeLineResponseDTO.builder()
                        .label(l.getLabel())
                        .amount(l.getAmount())
                        .build())
                .toList();
        boolean reprintAllowed = printCount > 0 && reprintCount < receiptProperties.getMaxReprints();
        return ReceiptDetailResponseDTO.builder()
                .paymentId(model.getPaymentId())
                .receiptNo(model.getReceiptNo())
                .paymentDate(model.getPaymentDate())
                .status(model.getPaymentStatus())
                .paymentMode(model.getPaymentMode())
                .amountPaid(model.getAmountPaid())
                .transactionRef(model.getTransactionRef())
                .chequeNo(model.getChequeNo())
                .chequeDate(model.getChequeDate())
                .bankName(model.getBankName())
                .remarks(model.getPaymentRemarks())
                .studentId(model.getStudentId())
                .admissionNo(model.getAdmissionNo())
                .studentName(model.getStudentName())
                .className(model.getClassName())
                .academicYear(model.getAcademicYearLabel())
                .term(model.getTermLabel())
                .feesPaymentStatus(model.getFeesPaymentStatus())
                .invoiceNo(model.getInvoiceNo())
                .invoiceDate(model.getInvoiceDate())
                .dueDate(model.getDueDate())
                .feeBreakdown(lines)
                .grossAmount(model.getGrossAmount())
                .discountAmount(model.getDiscountAmount())
                .lateFee(model.getLateFee())
                .netAmount(model.getNetAmount())
                .balanceAfterPayment(model.getBalanceAfterPayment())
                .collectedBy(model.getCollectedByName())
                .schoolName(model.getSchoolName())
                .schoolAddress(model.getSchoolAddress())
                .schoolPhone(model.getSchoolPhone())
                .totalPrintCount(printCount)
                .reprintAllowed(reprintAllowed)
                .build();
    }

    private ReceiptPrintHistoryItemDTO toHistoryItem(ReceiptPrintLog log) {
        return ReceiptPrintHistoryItemDTO.builder()
                .logId(log.getId())
                .receiptNo(log.getReceiptNo())
                .printerType(log.getPrinterType())
                .copyType(log.getCopyType())
                .printAction(log.getPrintAction())
                .renderedFormat(log.getRenderedFormat())
                .printCount(log.getPrintCount())
                .watermarkApplied(Boolean.TRUE.equals(log.getWatermarkApplied()))
                .printedBy(log.getPrintedBy())
                .printedAt(log.getPrintedAt())
                .remarks(log.getRemarks())
                .build();
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }

    /** Relative API path helper kept local to avoid circular constants dependency. */
    private static final class PaymentApiPaths {
        private PaymentApiPaths() {}

        static String receiptPdf(Long paymentId) {
            return "/api/v1/payments/receipts/" + paymentId + "/pdf";
        }
    }
}
