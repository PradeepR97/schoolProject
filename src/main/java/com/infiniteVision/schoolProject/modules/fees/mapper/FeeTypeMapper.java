package com.infiniteVision.schoolProject.modules.fees.mapper;

import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeTypeResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeType;
import org.springframework.stereotype.Component;

/**
 * Maps {@link FeeType} entities to API DTOs.
 */
@Component
public class FeeTypeMapper {

    public FeeType toEntity(CreateFeeTypeRequestDTO request) {
        return FeeType.builder()
                .feeTypeCode(normalizeCode(request.getFeeTypeCode()))
                .feeTypeName(request.getFeeTypeName().trim())
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .deleted(Boolean.FALSE)
                .build();
    }

    public void applyUpdates(FeeType entity, UpdateFeeTypeRequestDTO request) {
        if (request.getFeeTypeCode() != null) {
            entity.setFeeTypeCode(normalizeCode(request.getFeeTypeCode()));
        }
        if (request.getFeeTypeName() != null) {
            entity.setFeeTypeName(request.getFeeTypeName().trim());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        if (request.getDisplayOrder() != null) {
            entity.setDisplayOrder(request.getDisplayOrder());
        }
    }

    public FeeTypeResponseDTO toResponse(FeeType entity) {
        return FeeTypeResponseDTO.builder()
                .feeTypeId(entity.getId())
                .feeTypeCode(entity.getFeeTypeCode())
                .feeTypeName(entity.getFeeTypeName())
                .active(entity.getActive())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }

    private static String normalizeCode(String code) {
        return code != null ? code.trim().toUpperCase() : null;
    }
}
