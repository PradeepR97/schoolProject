package com.infiniteVision.schoolProject.modules.payment.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.payment.constants.PaymentApiConstants;
import com.infiniteVision.schoolProject.modules.payment.dto.request.CollectPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateInvoiceRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateLedgerRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.GenerateLedgerResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentReceiptResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.StudentFeeDuesResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.service.FeeLedgerService;
import com.infiniteVision.schoolProject.modules.payment.service.InvoiceService;
import com.infiniteVision.schoolProject.modules.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
 * REST APIs for fee ledger generation, invoicing, collection, and student dues.
 */
@RestController
@RequestMapping(value = PaymentApiConstants.PAYMENT_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PaymentController {

    private final FeeLedgerService feeLedgerService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    /** POST /api/v1/payments/ledgers/generate */
    @PostMapping("/ledgers/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<GenerateLedgerResponseDTO>> generateLedgers(
            @Valid @RequestBody GenerateLedgerRequestDTO request) {
        GenerateLedgerResponseDTO data = feeLedgerService.generateLedgers(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.FEE_LEDGER_GENERATED_SUCCESS, data));
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
}
