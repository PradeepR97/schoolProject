package com.infiniteVision.schoolProject.modules.scholarship.util;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeType;
import com.infiniteVision.schoolProject.modules.scholarship.entity.SchoolScheme;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ApplicableTo;
import com.infiniteVision.schoolProject.modules.scholarship.enums.DiscountType;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Computes scholarship discount amounts from {@link SchoolScheme} rules.
 */
public final class ScholarshipDiscountCalculator {

    private ScholarshipDiscountCalculator() {
    }

    public static BigDecimal calculateDiscountFromPercent(BigDecimal actualAmount, BigDecimal discountPercent) {
        if (actualAmount == null
                || actualAmount.compareTo(BigDecimal.ZERO) <= 0
                || discountPercent == null
                || discountPercent.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = actualAmount
                .multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return discount.min(actualAmount);
    }

    public static BigDecimal calculateDiscount(SchoolScheme scheme, BigDecimal actualAmount) {
        if (scheme == null || actualAmount == null || actualAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount;
        if (DiscountType.PERCENTAGE.equals(scheme.getDiscountType())) {
            discount = actualAmount
                    .multiply(scheme.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = scheme.getDiscountValue();
        }
        if (discount.compareTo(actualAmount) > 0) {
            return actualAmount;
        }
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return discount;
    }

    public static boolean schemeAppliesToFeeStructure(SchoolScheme scheme, FeeStructure structure) {
        if (scheme == null || structure == null || structure.getFeeType() == null) {
            return false;
        }
        if (Boolean.FALSE.equals(structure.getScholarshipAllowed())) {
            return false;
        }
        ApplicableTo applicableTo = scheme.getApplicableTo();
        if (ApplicableTo.ALL_FEES.equals(applicableTo)) {
            return true;
        }
        FeeType feeType = structure.getFeeType();
        if (ApplicableTo.TUITION_ONLY.equals(applicableTo)) {
            return isTuitionType(feeType);
        }
        if (ApplicableTo.SPECIFIC_HEAD.equals(applicableTo) && scheme.getFeeType() != null) {
            return scheme.getFeeType().getId().equals(feeType.getId());
        }
        return false;
    }

    public static boolean isTransportType(FeeType feeType) {
        if (feeType == null || feeType.getFeeTypeCode() == null) {
            return false;
        }
        return "TRANSPORT".equalsIgnoreCase(feeType.getFeeTypeCode().trim());
    }

    public static boolean isTuitionType(FeeType feeType) {
        if (feeType == null) {
            return false;
        }
        if (feeType.getFeeTypeCode() != null) {
            String code = feeType.getFeeTypeCode().toUpperCase();
            if (code.contains("TUITION") || code.contains("TUIT")) {
                return true;
            }
        }
        if (feeType.getFeeTypeName() != null) {
            return feeType.getFeeTypeName().toUpperCase().contains("TUITION");
        }
        return false;
    }
}
