package com.infiniteVision.schoolProject.modules.scholarship.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Reject a scholarship discount application.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectScholarshipApplicationRequestDTO {

    @NotBlank(message = "Rejection reason is required")
    @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
    private String rejectionReason;
}
