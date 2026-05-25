package com.infiniteVision.schoolProject.modules.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Cancel a successful payment and reverse ledger and invoice balances.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentRequestDTO {

    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    private String reason;
}
