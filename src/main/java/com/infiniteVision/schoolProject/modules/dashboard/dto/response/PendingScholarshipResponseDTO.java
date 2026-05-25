package com.infiniteVision.schoolProject.modules.dashboard.dto.response;

import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Scholarship application awaiting principal or correspondent action.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingScholarshipResponseDTO {

    private String studentName;
    private String schemeName;
    private BigDecimal amount;
    private ScholarshipApplicationStatus status;
}
