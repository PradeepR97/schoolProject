package com.infiniteVision.schoolProject.modules.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Amount to apply against one student fee ledger (one fee head) in a bulk collection.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAllocationRequestDTO {

    @NotNull(message = "Ledger id is required")
    private Long ledgerId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.01", message = "Amount paid must be greater than zero")
    private BigDecimal amountPaid;
}
