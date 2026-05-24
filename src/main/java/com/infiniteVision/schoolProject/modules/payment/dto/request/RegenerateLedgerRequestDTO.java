package com.infiniteVision.schoolProject.modules.payment.dto.request;

import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Recalculate unpaid fee ledgers from current active fee structure amounts.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegenerateLedgerRequestDTO {

    @NotNull(message = "Student id is required")
    private Long studentId;

    @NotNull(message = "Academic year id is required")
    private Long academicYearId;

    /** When set, only ledgers for this billing term are recalculated. */
    private FeeBillingTerm term;

    /** When set, only the ledger linked to this fee structure id is recalculated. */
    private Long structureId;
}
