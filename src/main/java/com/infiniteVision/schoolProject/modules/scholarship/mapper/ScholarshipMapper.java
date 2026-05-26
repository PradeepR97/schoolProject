package com.infiniteVision.schoolProject.modules.scholarship.mapper;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeType;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.entity.SchoolScheme;
import org.springframework.stereotype.Component;

/**
 * Maps {@link SchoolScheme} entities to API response DTOs.
 */
@Component
public class ScholarshipMapper {

    /**
     * Converts a persisted scheme to a response DTO (resolves related names when loaded).
     */
    public ScholarshipResponseDTO toResponse(SchoolScheme scheme) {
        FeeType feeType = scheme.getFeeType();
        return ScholarshipResponseDTO.builder()
                .schemeId(scheme.getId())
                .schemeName(scheme.getSchemeName())
                .schemeType(scheme.getSchemeType())
                .discountType(scheme.getDiscountType())
                .discountValue(scheme.getDiscountValue())
                .applicableTo(scheme.getApplicableTo())
                .feeTypeId(feeType != null ? feeType.getId() : null)
                .feeTypeName(feeType != null ? feeType.getFeeTypeName() : null)
                .academicYearId(scheme.getAcademicYear().getId())
                .academicYearName(scheme.getAcademicYear().getYearName())
                .isActive(scheme.getIsActive())
                .createdAt(scheme.getCreatedAt())
                .build();
    }
}
