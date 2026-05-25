package com.infiniteVision.schoolProject.modules.payment.receipt.service;

import com.infiniteVision.schoolProject.modules.payment.receipt.dto.request.PrintReceiptRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptPrintHistoryItemDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.dto.response.ReceiptPrintResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrinterType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.ReceiptCopyType;
import java.util.List;
import org.springframework.core.io.Resource;

public interface ReceiptPrintService {

    ReceiptDetailResponseDTO getReceiptDetails(Long paymentId);

    ReceiptPrintResponseDTO printReceipt(Long paymentId, PrintReceiptRequestDTO request);

    ReceiptPrintResponseDTO reprintReceipt(Long paymentId, PrintReceiptRequestDTO request);

    Resource downloadPdf(Long paymentId, PrinterType printerType, ReceiptCopyType copyType);

    List<ReceiptPrintHistoryItemDTO> getPrintHistory(Long paymentId);
}
