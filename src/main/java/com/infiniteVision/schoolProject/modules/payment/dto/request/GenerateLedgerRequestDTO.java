package com.infiniteVision.schoolProject.modules.payment.dto.request;

import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request to generate {@code student_fee_ledger} rows from fee structures.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateLedgerRequestDTO {

    @NotNull(message = "Student id is required")
    private Long studentId;

    @NotNull(message = "Academic year id is required")
    private Long academicYearId;

    /** When set, only structures mapped to this billing term are generated. */
    private FeeBillingTerm term;
}
