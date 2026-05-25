package com.infiniteVision.schoolProject.modules.scholarship.dto.request;

import com.infiniteVision.schoolProject.modules.scholarship.enums.ApplicableTo;
import com.infiniteVision.schoolProject.modules.scholarship.enums.DiscountType;
import com.infiniteVision.schoolProject.modules.scholarship.enums.SchemeType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for creating a scholarship scheme.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScholarshipRequestDTO {

    @NotBlank(message = "Scheme name is required")
    @Size(max = 150, message = "Scheme name must not exceed 150 characters")
    private String schemeName;

    @NotNull(message = "Scheme type is required")
    private SchemeType schemeType;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @PositiveOrZero(message = "Discount value must be zero or positive")
    @DecimalMax(value = "9999999999.99", message = "Discount value is too large")
    private BigDecimal discountValue;

    @NotNull(message = "Applicable to is required")
    private ApplicableTo applicableTo;

    private Long feeHeadId;

    @NotNull(message = "Academic year id is required")
    private Long academicYearId;

    private Boolean isActive;
}
