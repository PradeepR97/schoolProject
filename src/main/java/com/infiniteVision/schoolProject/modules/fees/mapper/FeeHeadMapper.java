package com.infiniteVision.schoolProject.modules.fees.mapper;

import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeHeadResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import org.springframework.stereotype.Component;

/**
 * Maps {@link FeeHead} entities to API DTOs.
 */
@Component
public class FeeHeadMapper {

    public FeeHead toEntity(CreateFeeHeadRequestDTO request) {
        return FeeHead.builder()
                .feeHeadCode(normalizeCode(request.getFeeHeadCode()))
                .feeHeadName(request.getFeeHeadName().trim())
                .description(request.getDescription())
                .feeCategory(request.getFeeCategory())
                .mandatory(request.getMandatory() != null ? request.getMandatory() : Boolean.TRUE)
                .refundable(request.getRefundable() != null ? request.getRefundable() : Boolean.FALSE)
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .deleted(Boolean.FALSE)
                .build();
    }

    public void applyUpdates(FeeHead entity, UpdateFeeHeadRequestDTO request) {
        if (request.getFeeHeadCode() != null) {
            entity.setFeeHeadCode(normalizeCode(request.getFeeHeadCode()));
        }
        if (request.getFeeHeadName() != null) {
            entity.setFeeHeadName(request.getFeeHeadName().trim());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getFeeCategory() != null) {
            entity.setFeeCategory(request.getFeeCategory());
        }
        if (request.getMandatory() != null) {
            entity.setMandatory(request.getMandatory());
        }
        if (request.getRefundable() != null) {
            entity.setRefundable(request.getRefundable());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        if (request.getDisplayOrder() != null) {
            entity.setDisplayOrder(request.getDisplayOrder());
        }
    }

    public FeeHeadResponseDTO toResponse(FeeHead entity) {
        return FeeHeadResponseDTO.builder()
                .feeHeadId(entity.getId())
                .feeHeadCode(entity.getFeeHeadCode())
                .feeHeadName(entity.getFeeHeadName())
                .description(entity.getDescription())
                .feeCategory(entity.getFeeCategory())
                .mandatory(entity.getMandatory())
                .refundable(entity.getRefundable())
                .active(entity.getActive())
                .displayOrder(entity.getDisplayOrder())
                .build();
    }

    private static String normalizeCode(String code) {
        return code != null ? code.trim().toUpperCase() : null;
    }
}
