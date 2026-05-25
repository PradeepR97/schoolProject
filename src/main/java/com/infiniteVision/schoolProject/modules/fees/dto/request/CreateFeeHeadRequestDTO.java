package com.infiniteVision.schoolProject.modules.fees.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body to create a fee head master record.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeeHeadRequestDTO {

    @NotBlank(message = "Fee head code is required")
    @Size(max = 30, message = "Fee head code must not exceed 30 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Fee head code must be uppercase letters, digits, or underscore")
    private String feeHeadCode;

    @NotBlank(message = "Fee head name is required")
    @Size(max = 100, message = "Fee head name must not exceed 100 characters")
    private String feeHeadName;

    private String description;

    @Size(max = 50, message = "Fee category must not exceed 50 characters")
    private String feeCategory;

    private Boolean mandatory;

    private Boolean refundable;

    private Boolean active;

    private Integer displayOrder;
}
