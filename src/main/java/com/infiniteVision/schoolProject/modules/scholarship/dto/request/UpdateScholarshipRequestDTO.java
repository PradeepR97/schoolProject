package com.infiniteVision.schoolProject.modules.scholarship.dto.request;

import com.infiniteVision.schoolProject.modules.scholarship.enums.ApplicableTo;
import com.infiniteVision.schoolProject.modules.scholarship.enums.DiscountType;
import com.infiniteVision.schoolProject.modules.scholarship.enums.SchemeType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Partial update body for a scholarship scheme. At least one field must be provided (enforced in service).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScholarshipRequestDTO {

    @Size(max = 150, message = "Scheme name must not exceed 150 characters")
    private String schemeName;

    private SchemeType schemeType;

    private DiscountType discountType;

    @PositiveOrZero(message = "Discount value must be zero or positive")
    @DecimalMax(value = "9999999999.99", message = "Discount value is too large")
    private BigDecimal discountValue;

    private ApplicableTo applicableTo;

    private Long feeHeadId;

    private Long academicYearId;

    private Boolean isActive;
}
