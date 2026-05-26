package com.infiniteVision.schoolProject.modules.fees.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bulk create multiple fee types in one request.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCreateFeeTypeRequestDTO {

    @NotEmpty(message = "At least one fee type is required")
    @Valid
    private List<CreateFeeTypeRequestDTO> feeTypes;
}
