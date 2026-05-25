package com.infiniteVision.schoolProject.modules.fees.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Create multiple fee heads in one request (school fee document categories).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCreateFeeHeadRequestDTO {

    @Valid
    @NotEmpty(message = "At least one fee head is required")
    private List<CreateFeeHeadRequestDTO> feeHeads;
}
