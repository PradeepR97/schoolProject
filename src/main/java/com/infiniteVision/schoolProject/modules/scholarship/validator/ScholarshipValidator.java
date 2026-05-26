package com.infiniteVision.schoolProject.modules.scholarship.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeType;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ApplicableTo;
import com.infiniteVision.schoolProject.modules.scholarship.enums.DiscountType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Business validation rules for scholarship schemes.
 */
@Component
public class ScholarshipValidator {

    /**
     * Validates fee type presence rules for the given applicable scope.
     */
    public void validateFeeTypeForApplicableTo(ApplicableTo applicableTo, Long feeTypeId, FeeType feeType) {
        List<String> errors = new ArrayList<>();
        if (ApplicableTo.SPECIFIC_HEAD.equals(applicableTo)) {
            if (feeTypeId == null) {
                errors.add(MessageConstants.FEE_TYPE_REQUIRED_FOR_SPECIFIC_HEAD);
            } else if (feeType == null) {
                errors.add(MessageConstants.FEE_TYPE_NOT_FOUND);
            }
        } else if (feeTypeId != null && feeType == null) {
            errors.add(MessageConstants.FEE_TYPE_NOT_FOUND);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    /**
     * Validates discount value against discount type (percentage must not exceed 100).
     */
    public void validateDiscountValue(DiscountType discountType, BigDecimal discountValue) {
        if (discountType == null || discountValue == null) {
            return;
        }
        if (DiscountType.PERCENTAGE.equals(discountType)
                && discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED,
                    List.of(MessageConstants.DISCOUNT_PERCENTAGE_EXCEEDS_100));
        }
    }
}
