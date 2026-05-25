package com.infiniteVision.schoolProject.modules.payment.receipt.dto.request;

import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrinterType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.ReceiptCopyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrintReceiptRequestDTO {

    @NotNull(message = "Printer type is required")
    private PrinterType printerType;

    @NotNull(message = "Copy type is required")
    private ReceiptCopyType copyType;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
