package com.infiniteVision.schoolProject.modules.payment.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Result of recalculating unpaid ledgers from updated fee structures.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegenerateLedgerResponseDTO {

    private Long studentId;
    private Long academicYearId;
    private int updatedCount;
    private int skippedCount;
    private List<FeeLedgerSummaryResponseDTO> ledgers;
}
