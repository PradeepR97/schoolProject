package com.infiniteVision.schoolProject.modules.fees.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeType;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeTypeRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Business validation for fee type create and update.
 */
@Component
@RequiredArgsConstructor
public class FeeTypeValidator {

    private final FeeTypeRepository feeTypeRepository;

    /**
     * Validates create request including unique fee type code.
     */
    public void validateCreate(CreateFeeTypeRequestDTO request) {
        List<String> errors = new ArrayList<>();
        String code = request.getFeeTypeCode() != null ? request.getFeeTypeCode().trim().toUpperCase() : "";
        if (feeTypeRepository.existsByFeeTypeCodeAndDeletedFalse(code)) {
            errors.add(MessageConstants.FEE_TYPE_CODE_ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    /**
     * Validates partial update; ensures code uniqueness when code changes.
     */
    public void validateUpdate(Long feeTypeId, UpdateFeeTypeRequestDTO request, FeeType existing) {
        List<String> errors = new ArrayList<>();

        if (!hasAnyField(request)) {
            errors.add(MessageConstants.FEE_TYPE_UPDATE_EMPTY);
        }

        if (request.getFeeTypeCode() != null) {
            String code = request.getFeeTypeCode().trim().toUpperCase();
            if (feeTypeRepository.existsByFeeTypeCodeAndDeletedFalse(code)
                    && !code.equals(existing.getFeeTypeCode())) {
                errors.add(MessageConstants.FEE_TYPE_CODE_ALREADY_EXISTS);
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    private static boolean hasAnyField(UpdateFeeTypeRequestDTO request) {
        return request.getFeeTypeCode() != null
                || request.getFeeTypeName() != null
                || request.getActive() != null
                || request.getDisplayOrder() != null;
    }
}
