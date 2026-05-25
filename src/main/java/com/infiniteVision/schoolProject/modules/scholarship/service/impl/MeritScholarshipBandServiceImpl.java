package com.infiniteVision.schoolProject.modules.scholarship.service.impl;

import com.infiniteVision.schoolProject.modules.scholarship.dto.response.MeritBandResolveResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.MeritBandResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.entity.ScholarshipMeritBand;
import com.infiniteVision.schoolProject.modules.scholarship.repository.ScholarshipMeritBandRepository;
import com.infiniteVision.schoolProject.modules.scholarship.service.MeritScholarshipBandService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads merit bands from DB and resolves discount % for a given mark (no hardcoded thresholds).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MeritScholarshipBandServiceImpl implements MeritScholarshipBandService {

    private final ScholarshipMeritBandRepository meritBandRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MeritBandResponseDTO> listBands(Long academicYearId) {
        return meritBandRepository.findActiveByAcademicYearIdOrderByMinMarkDesc(academicYearId).stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Finds the highest matching band for marks in the given academic year.
     */
    @Override
    @Transactional(readOnly = true)
    public MeritBandResolveResponseDTO resolveDiscountPercent(Long academicYearId, BigDecimal marks) {
        if (marks == null) {
            return MeritBandResolveResponseDTO.builder()
                    .marks(null)
                    .matched(false)
                    .build();
        }
        Optional<ScholarshipMeritBand> band =
                meritBandRepository.findMatchingBand(academicYearId, marks);
        if (band.isEmpty()) {
            log.debug("No merit band matched yearId={} marks={}", academicYearId, marks);
            return MeritBandResolveResponseDTO.builder()
                    .marks(marks)
                    .matched(false)
                    .build();
        }
        ScholarshipMeritBand matched = band.get();
        return MeritBandResolveResponseDTO.builder()
                .marks(marks)
                .discountPercent(matched.getDiscountPercent())
                .bandLabel(matched.getBandLabel())
                .matched(true)
                .build();
    }

    private MeritBandResponseDTO toResponse(ScholarshipMeritBand band) {
        return MeritBandResponseDTO.builder()
                .bandId(band.getId())
                .academicYearId(band.getAcademicYear().getId())
                .minMark(band.getMinMark())
                .maxMark(band.getMaxMark())
                .discountPercent(band.getDiscountPercent())
                .bandLabel(band.getBandLabel())
                .build();
    }
}
