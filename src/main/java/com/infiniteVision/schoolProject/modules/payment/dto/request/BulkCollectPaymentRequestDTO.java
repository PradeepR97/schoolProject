package com.infiniteVision.schoolProject.modules.payment.dto.request;

import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Collect payment against multiple ledgers (fee heads) in one transaction.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCollectPaymentRequestDTO {

    @NotNull(message = "Student id is required")
    private Long studentId;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    private String transactionRef;
    private String chequeNo;
    private LocalDate chequeDate;
    private String bankName;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    /** Idempotent replay for the entire bulk batch. */
    @Size(max = 64, message = "Idempotency key must not exceed 64 characters")
    private String idempotencyKey;

    private Boolean autoGenerateInvoice;

    @Valid
    @NotEmpty(message = "At least one ledger allocation is required")
    private List<PaymentAllocationRequestDTO> allocations;
}
