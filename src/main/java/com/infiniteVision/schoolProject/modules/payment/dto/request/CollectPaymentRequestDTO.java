package com.infiniteVision.schoolProject.modules.payment.dto.request;

import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request to record a fee payment against a student ledger.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectPaymentRequestDTO {

    @NotNull(message = "Student id is required")
    private Long studentId;

    @NotNull(message = "Ledger id is required")
    private Long ledgerId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.01", message = "Amount paid must be greater than zero")
    private BigDecimal amountPaid;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    private String transactionRef;
    private String chequeNo;
    private LocalDate chequeDate;
    private String bankName;
    private String remarks;

    /** Client-generated key; duplicate requests return the original receipt. */
    private String idempotencyKey;

    /** When true and no invoice exists, one is created before collection. */
    private Boolean autoGenerateInvoice;
}
