package com.infiniteVision.schoolProject.modules.scholarship.dto.response;

import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Per-application outcome for bulk approve/reject.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipApplicationActionResultDTO {

    private Long applicationId;
    private Long studentId;
    private ScholarshipApplicationStatus previousStatus;
    private ScholarshipApplicationStatus newStatus;
    private boolean success;
    private String message;
}
