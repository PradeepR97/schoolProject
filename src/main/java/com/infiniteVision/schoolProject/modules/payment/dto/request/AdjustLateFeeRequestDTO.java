package com.infiniteVision.schoolProject.modules.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Manual late-fee adjustment on an unpaid or partially paid fee ledger.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustLateFeeRequestDTO {

    @NotNull(message = "Late fee amount is required")
    @DecimalMin(value = "0.00", message = "Late fee must be zero or greater")
    private BigDecimal lateFee;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}
