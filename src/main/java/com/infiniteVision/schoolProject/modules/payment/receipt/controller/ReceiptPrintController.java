package com.infiniteVision.schoolProject.modules.payment.receipt.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.payment.constants.PaymentApiConstants;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.request.PrintReceiptRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptPrintHistoryItemDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptPrintResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrinterType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.ReceiptCopyType;
import com.infiniteVision.schoolProject.modules.payment.receipt.service.ReceiptPrintService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
 * Receipt printing: preview, print, PDF download, reprint, and print history.
 */
@RestController
@RequestMapping(PaymentApiConstants.RECEIPTS)
@RequiredArgsConstructor
public class ReceiptPrintController {

    private final ReceiptPrintService receiptPrintService;

    /** GET /api/v1/payments/receipts/{paymentId}/details */
    @GetMapping("/{paymentId}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<ReceiptDetailResponseDTO>> getDetails(@PathVariable Long paymentId) {
        ReceiptDetailResponseDTO data = receiptPrintService.getReceiptDetails(paymentId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.RECEIPT_DETAILS_RETRIEVED_SUCCESS, data));
    }

    /** POST /api/v1/payments/receipts/{paymentId}/print */
    @PostMapping("/{paymentId}/print")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<ReceiptPrintResponseDTO>> print(
            @PathVariable Long paymentId, @Valid @RequestBody PrintReceiptRequestDTO request) {
        ReceiptPrintResponseDTO data = receiptPrintService.printReceipt(paymentId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.RECEIPT_PRINTED_SUCCESS, data));
    }

    /** POST /api/v1/payments/receipts/{paymentId}/reprint */
    @PostMapping("/{paymentId}/reprint")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<ReceiptPrintResponseDTO>> reprint(
            @PathVariable Long paymentId, @Valid @RequestBody PrintReceiptRequestDTO request) {
        ReceiptPrintResponseDTO data = receiptPrintService.reprintReceipt(paymentId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.RECEIPT_REPRINTED_SUCCESS, data));
    }

    /** GET /api/v1/payments/receipts/{paymentId}/pdf */
    @GetMapping("/{paymentId}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<Resource> downloadPdf(
            @PathVariable Long paymentId,
            @RequestParam(defaultValue = "LASER") PrinterType printerType,
            @RequestParam(defaultValue = "ORIGINAL") ReceiptCopyType copyType) {
        Resource resource = receiptPrintService.downloadPdf(paymentId, printerType, copyType);
        String filename = "receipt-" + paymentId + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    /** GET /api/v1/payments/receipts/{paymentId}/print-history */
    @GetMapping("/{paymentId}/print-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<ReceiptPrintHistoryItemDTO>>> printHistory(@PathVariable Long paymentId) {
        List<ReceiptPrintHistoryItemDTO> data = receiptPrintService.getPrintHistory(paymentId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.RECEIPT_PRINT_HISTORY_RETRIEVED_SUCCESS, data));
    }
}
