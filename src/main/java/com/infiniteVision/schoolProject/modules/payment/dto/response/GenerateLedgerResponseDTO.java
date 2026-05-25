package com.infiniteVision.schoolProject.modules.payment.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Result of ledger generation for a student.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateLedgerResponseDTO {

    private Long studentId;
    private Long academicYearId;
    private int createdCount;
    private int skippedCount;
    private List<FeeLedgerSummaryResponseDTO> ledgers;
}
