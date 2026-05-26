package com.infiniteVision.schoolProject.modules.fees.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body to create a fee type master record.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeeTypeRequestDTO {

    @NotBlank(message = "Fee type code is required")
    @Size(max = 30, message = "Fee type code must not exceed 30 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Fee type code must be uppercase letters, digits, or underscore")
    private String feeTypeCode;

    @NotBlank(message = "Fee type name is required")
    @Size(max = 100, message = "Fee type name must not exceed 100 characters")
    private String feeTypeName;

    private Boolean active;

    private Integer displayOrder;
}
