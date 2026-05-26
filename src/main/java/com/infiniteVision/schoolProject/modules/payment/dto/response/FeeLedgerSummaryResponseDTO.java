package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Summary of a student fee ledger row.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeLedgerSummaryResponseDTO {

    private Long ledgerId;
    private Long structureId;
    private String feeTypeName;
    private FeeBillingTerm term;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private LocalDate dueDate;
    private LedgerStatus status;
}
