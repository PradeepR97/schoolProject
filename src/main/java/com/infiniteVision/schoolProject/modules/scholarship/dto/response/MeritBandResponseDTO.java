package com.infiniteVision.schoolProject.modules.scholarship.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Merit mark band configuration row.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeritBandResponseDTO {

    private Long bandId;
    private Long academicYearId;
    private BigDecimal minMark;
    private BigDecimal maxMark;
    private BigDecimal discountPercent;
    private String bandLabel;
}
