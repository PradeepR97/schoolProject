package com.infiniteVision.schoolProject.modules.scholarship.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Summary of bulk scholarship application actions.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkScholarshipApplicationResponseDTO {

    private int requestedCount;
    private int successCount;
    private int skippedCount;
    private int failedCount;
    private List<ScholarshipApplicationActionResultDTO> results;
}
