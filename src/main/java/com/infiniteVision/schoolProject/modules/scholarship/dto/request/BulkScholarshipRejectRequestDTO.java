package com.infiniteVision.schoolProject.modules.scholarship.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Bulk reject scholarship discount applications.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkScholarshipRejectRequestDTO {

    @NotEmpty(message = "At least one application id is required")
    @Size(max = 100, message = "Cannot reject more than 100 applications at once")
    private List<@NotNull Long> applicationIds;

    @NotBlank(message = "Rejection reason is required")
    @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
    private String rejectionReason;
}
