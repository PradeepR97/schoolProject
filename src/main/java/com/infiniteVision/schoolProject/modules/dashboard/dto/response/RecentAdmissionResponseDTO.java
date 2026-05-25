package com.infiniteVision.schoolProject.modules.dashboard.dto.response;

import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Recent student admission row for the dashboard.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentAdmissionResponseDTO {

    private String applicationNo;
    private String studentName;
    private String className;
    private StudentStatus status;
}
