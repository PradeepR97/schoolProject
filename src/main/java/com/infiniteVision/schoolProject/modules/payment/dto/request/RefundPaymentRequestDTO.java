package com.infiniteVision.schoolProject.modules.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Refund a successful payment and reverse ledger and invoice balances.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentRequestDTO {

    @NotBlank(message = "Refund reason is required")
    @Size(max = 500, message = "Refund reason must not exceed 500 characters")
    private String reason;
}
