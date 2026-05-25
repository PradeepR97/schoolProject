package com.infiniteVision.schoolProject.modules.scholarship.service;

import com.infiniteVision.schoolProject.modules.scholarship.dto.response.MeritBandResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.MeritBandResolveResponseDTO;
import java.math.BigDecimal;
import java.util.List;

/**
 * Resolves tuition waiver percentages from configured merit mark bands.
 */
public interface MeritScholarshipBandService {

    List<MeritBandResponseDTO> listBands(Long academicYearId);

    MeritBandResolveResponseDTO resolveDiscountPercent(Long academicYearId, BigDecimal marks);
}
