package com.infiniteVision.schoolProject.modules.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request to generate an invoice for a fee ledger row.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateInvoiceRequestDTO {

    @NotNull(message = "Ledger id is required")
    private Long ledgerId;
}
