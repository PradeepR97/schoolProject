package com.infiniteVision.schoolProject.modules.scholarship.dto.response;

import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApprovalAction;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * One row in the scholarship approval timeline.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipApprovalHistoryResponseDTO {

    private Long historyId;
    private Long applicationId;
    private ScholarshipApprovalAction action;
    private String performedBy;
    private LocalDateTime performedAt;
    private String remarks;
    private ScholarshipApplicationStatus previousStatus;
    private ScholarshipApplicationStatus newStatus;
}
