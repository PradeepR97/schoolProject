package com.infiniteVision.schoolProject.modules.fees.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeHeadRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Business validation for fee head create and update.
 */
@Component
@RequiredArgsConstructor
public class FeeHeadValidator {

    private final FeeHeadRepository feeHeadRepository;

    /**
     * Validates create request including unique fee head code.
     */
    public void validateCreate(CreateFeeHeadRequestDTO request) {
        List<String> errors = new ArrayList<>();
        String code = request.getFeeHeadCode() != null ? request.getFeeHeadCode().trim().toUpperCase() : "";
        if (feeHeadRepository.existsByFeeHeadCodeAndDeletedFalse(code)) {
            errors.add(MessageConstants.FEE_HEAD_CODE_ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    /**
     * Validates partial update; ensures code uniqueness when code changes.
     */
    public void validateUpdate(Long feeHeadId, UpdateFeeHeadRequestDTO request, FeeHead existing) {
        List<String> errors = new ArrayList<>();

        if (!hasAnyField(request)) {
            errors.add(MessageConstants.FEE_HEAD_UPDATE_EMPTY);
        }

        if (request.getFeeHeadCode() != null) {
            String code = request.getFeeHeadCode().trim().toUpperCase();
            if (feeHeadRepository.existsByFeeHeadCodeAndDeletedFalse(code)
                    && !code.equals(existing.getFeeHeadCode())) {
                errors.add(MessageConstants.FEE_HEAD_CODE_ALREADY_EXISTS);
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    private static boolean hasAnyField(UpdateFeeHeadRequestDTO request) {
        return request.getFeeHeadCode() != null
                || request.getFeeHeadName() != null
                || request.getDescription() != null
                || request.getFeeCategory() != null
                || request.getMandatory() != null
                || request.getRefundable() != null
                || request.getActive() != null
                || request.getDisplayOrder() != null;
    }
}
