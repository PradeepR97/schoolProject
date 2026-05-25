package com.infiniteVision.schoolProject.modules.scholarship.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Bulk approve scholarship discount applications.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkScholarshipApprovalRequestDTO {

    @NotEmpty(message = "At least one application id is required")
    @Size(max = 100, message = "Cannot approve more than 100 applications at once")
    private List<@NotNull Long> applicationIds;

    private String remarks;
}
