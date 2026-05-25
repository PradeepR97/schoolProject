package com.infiniteVision.schoolProject.modules.scholarship.dto.response;

import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Student scholarship application API response.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipApplicationResponseDTO {

    private Long applicationId;
    private Long studentId;
    private String admissionNo;
    private String studentName;
    private Long schemeId;
    private String schemeName;
    private Long academicYearId;
    private String academicYearName;
    private ScholarshipApplicationStatus status;
    private LocalDateTime appliedAt;
    private String applicationRemarks;
    private String principalApprovedBy;
    private LocalDateTime principalApprovedAt;
    private String correspondentApprovedBy;
    private LocalDateTime correspondentApprovedAt;
    private String rejectedBy;
    private LocalDateTime rejectedAt;
    private String rejectionReason;
}
