package com.infiniteVision.schoolProject.modules.fees.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Partial update body for a fee type master record.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeeTypeRequestDTO {

    @Size(max = 30, message = "Fee type code must not exceed 30 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Fee type code must be uppercase letters, digits, or underscore")
    private String feeTypeCode;

    @Size(max = 100, message = "Fee type name must not exceed 100 characters")
    private String feeTypeName;

    private Boolean active;

    private Integer displayOrder;
}
