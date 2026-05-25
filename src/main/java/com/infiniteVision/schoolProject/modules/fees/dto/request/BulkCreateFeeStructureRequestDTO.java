package com.infiniteVision.schoolProject.modules.fees.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Bulk import of fee structure rows (amounts from school fee document, not hardcoded in code).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCreateFeeStructureRequestDTO {

    @Valid
    @NotEmpty(message = "At least one fee structure row is required")
    private List<CreateFeeStructureRequestDTO> structures;
}
