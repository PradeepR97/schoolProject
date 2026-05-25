package com.infiniteVision.schoolProject.modules.payment.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.payment.constants.PaymentApiConstants;
import com.infiniteVision.schoolProject.modules.payment.dto.request.AdjustLateFeeRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.CancelPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.CollectPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateInvoiceRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateLedgerRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.RefundPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.RegenerateLedgerRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.GenerateLedgerResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentReceiptResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.RegenerateLedgerResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.StudentFeeDuesResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.payment.service.FeeLedgerService;
import com.infiniteVision.schoolProject.modules.payment.service.InvoiceService;
import com.infiniteVision.schoolProject.modules.payment.service.PaymentService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST APIs for fee ledger generation, invoicing, collection, listing, reversal, and student dues.
 */
@RestController
@RequestMapping(value = PaymentApiConstants.PAYMENT_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentController {

    private final FeeLedgerService feeLedgerService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    /** GET /api/v1/payments — paginated payment list. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<PagedResponseDTO<PaymentListItemResponseDTO>>> listPayments(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long ledgerId,
            @RequestParam(required = false) Long invoiceId,
            @RequestParam(required = false) PaymentRecordStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponseDTO<PaymentListItemResponseDTO> data = paymentService.listPayments(
                studentId, ledgerId, invoiceId, status, fromDate, toDate, page, size);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.PAYMENTS_LISTED_SUCCESS, data));
    }

    /** GET /api/v1/payments/invoices — paginated invoice list. */
    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<PagedResponseDTO<InvoiceListItemResponseDTO>>> listInvoices(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponseDTO<InvoiceListItemResponseDTO> data =
                invoiceService.listInvoices(studentId, academicYearId, status, page, size);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.INVOICES_LISTED_SUCCESS, data));
    }

    /** GET /api/v1/payments/invoices/{invoiceId} */
    @GetMapping("/invoices/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<InvoiceDetailResponseDTO>> getInvoiceById(@PathVariable Long invoiceId) {
        InvoiceDetailResponseDTO data = invoiceService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.INVOICE_RETRIEVED_SUCCESS, data));
    }

    /** POST /api/v1/payments/ledgers/generate */
    @PostMapping("/ledgers/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<GenerateLedgerResponseDTO>> generateLedgers(
            @Valid @RequestBody GenerateLedgerRequestDTO request) {
        GenerateLedgerResponseDTO data = feeLedgerService.generateLedgers(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.FEE_LEDGER_GENERATED_SUCCESS, data));
    }

    /** POST /api/v1/payments/ledgers/regenerate — recalculate unpaid ledgers from fee structures. */
    @PostMapping("/ledgers/regenerate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<RegenerateLedgerResponseDTO>> regenerateLedgers(
            @Valid @RequestBody RegenerateLedgerRequestDTO request) {
        RegenerateLedgerResponseDTO data = feeLedgerService.regenerateLedgers(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_LEDGER_REGENERATED_SUCCESS, data));
    }

    /** GET /api/v1/payments/ledgers/{ledgerId} */
    @GetMapping("/ledgers/{ledgerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<FeeLedgerDetailResponseDTO>> getLedgerById(@PathVariable Long ledgerId) {
        FeeLedgerDetailResponseDTO data = feeLedgerService.getLedgerById(ledgerId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_LEDGER_RETRIEVED_SUCCESS, data));
    }

    /** POST /api/v1/payments/ledgers/{ledgerId}/late-fee — manual late-fee adjustment. */
    @PostMapping("/ledgers/{ledgerId}/late-fee")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<FeeLedgerSummaryResponseDTO>> adjustLateFee(
            @PathVariable Long ledgerId, @Valid @RequestBody AdjustLateFeeRequestDTO request) {
        FeeLedgerSummaryResponseDTO data = feeLedgerService.adjustLateFee(ledgerId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LATE_FEE_ADJUSTED_SUCCESS, data));
    }

    /** POST /api/v1/payments/invoices/generate */
    @PostMapping("/invoices/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<InvoiceSummaryResponseDTO>> generateInvoice(
            @Valid @RequestBody GenerateInvoiceRequestDTO request) {
        InvoiceSummaryResponseDTO data = invoiceService.generateInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.INVOICE_GENERATED_SUCCESS, data));
    }

    /** POST /api/v1/payments/collect */
    @PostMapping("/collect")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<PaymentReceiptResponseDTO>> collectPayment(
            @Valid @RequestBody CollectPaymentRequestDTO request) {
        PaymentReceiptResponseDTO data = paymentService.collectPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.PAYMENT_COLLECTED_SUCCESS, data));
    }

    /** GET /api/v1/payments/students/{studentId}/dues */
    @GetMapping("/students/{studentId}/dues")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<StudentFeeDuesResponseDTO>> getStudentDues(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long academicYearId) {
        StudentFeeDuesResponseDTO data = paymentService.getStudentDues(studentId, academicYearId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.STUDENT_DUES_RETRIEVED_SUCCESS, data));
    }

    /** GET /api/v1/payments/receipts/{paymentId} */
    @GetMapping("/receipts/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<PaymentReceiptResponseDTO>> getReceipt(@PathVariable Long paymentId) {
        PaymentReceiptResponseDTO data = paymentService.getReceiptById(paymentId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.PAYMENT_RECEIPT_RETRIEVED_SUCCESS, data));
    }

    /** POST /api/v1/payments/{paymentId}/cancel */
    @PostMapping("/{paymentId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<PaymentReceiptResponseDTO>> cancelPayment(
            @PathVariable Long paymentId, @Valid @RequestBody CancelPaymentRequestDTO request) {
        PaymentReceiptResponseDTO data = paymentService.cancelPayment(paymentId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.PAYMENT_CANCELLED_SUCCESS, data));
    }

    /** POST /api/v1/payments/{paymentId}/refund */
    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<PaymentReceiptResponseDTO>> refundPayment(
            @PathVariable Long paymentId, @Valid @RequestBody RefundPaymentRequestDTO request) {
        PaymentReceiptResponseDTO data = paymentService.refundPayment(paymentId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.PAYMENT_REFUNDED_SUCCESS, data));
    }
}
