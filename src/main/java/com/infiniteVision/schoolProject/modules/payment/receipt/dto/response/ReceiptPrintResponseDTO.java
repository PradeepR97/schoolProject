package com.infiniteVision.schoolProject.modules.payment.receipt.dto.response;

import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrintAction;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrinterType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.ReceiptCopyType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.RenderedFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptPrintResponseDTO {

    private Long paymentId;
    private String receiptNo;
    private PrinterType printerType;
    private ReceiptCopyType copyType;
    private PrintAction printAction;
    private RenderedFormat renderedFormat;
    private String plainTextContent;
    private String pdfFileName;
    private String pdfDownloadPath;
    private int printSequence;
    private boolean watermarkApplied;
    private LocalDateTime printedAt;
}
