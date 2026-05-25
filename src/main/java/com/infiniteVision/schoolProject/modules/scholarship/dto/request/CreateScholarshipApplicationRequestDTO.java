package com.infiniteVision.schoolProject.modules.scholarship.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Create a student scholarship discount application.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScholarshipApplicationRequestDTO {

    @NotNull(message = "Student id is required")
    private Long studentId;

    @NotNull(message = "Scheme id is required")
    private Long schemeId;

    @NotNull(message = "Academic year id is required")
    private Long academicYearId;

    @Size(max = 2000, message = "Remarks must not exceed 2000 characters")
    private String applicationRemarks;
}
