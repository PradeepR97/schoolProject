package com.infiniteVision.schoolProject.modules.scholarship.dto.response;

import com.infiniteVision.schoolProject.modules.scholarship.enums.ApplicableTo;
import com.infiniteVision.schoolProject.modules.scholarship.enums.DiscountType;
import com.infiniteVision.schoolProject.modules.scholarship.enums.SchemeType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Scholarship scheme API response.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipResponseDTO {

    private Long schemeId;
    private String schemeName;
    private SchemeType schemeType;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private ApplicableTo applicableTo;
    private Long feeTypeId;
    private String feeTypeName;
    private Long academicYearId;
    private String academicYearName;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
