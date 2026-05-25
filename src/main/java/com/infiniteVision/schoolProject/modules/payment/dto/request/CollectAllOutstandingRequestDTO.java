package com.infiniteVision.schoolProject.modules.payment.dto.request;

import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Pay full balance on every outstanding fee-head ledger for a student in one bulk transaction.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectAllOutstandingRequestDTO {

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    /** When null, uses the student's current academic year. */
    private Long academicYearId;

    private String transactionRef;
    private String chequeNo;
    private LocalDate chequeDate;
    private String bankName;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    @Size(max = 64, message = "Idempotency key must not exceed 64 characters")
    private String idempotencyKey;

    private Boolean autoGenerateInvoice;
}
