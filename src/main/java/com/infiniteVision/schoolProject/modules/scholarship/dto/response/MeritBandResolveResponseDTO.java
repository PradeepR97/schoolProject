package com.infiniteVision.schoolProject.modules.scholarship.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Result of resolving a student's marks to a merit discount percent.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeritBandResolveResponseDTO {

    private BigDecimal marks;
    private BigDecimal discountPercent;
    private String bandLabel;
    private boolean matched;
}
