package com.infiniteVision.schoolProject.modules.payment.util;

import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;

/**
 * Maps fee structure {@link TermType} to payment ledger/invoice {@link FeeBillingTerm}.
 */
public final class PaymentTermMapper {

    private PaymentTermMapper() {
    }

    public static FeeBillingTerm toBillingTerm(TermType termType) {
        if (termType == null) {
            return null;
        }
        return switch (termType) {
            case TERM_1 -> FeeBillingTerm.TERM1;
            case TERM_2 -> FeeBillingTerm.TERM2;
            case TERM_3 -> FeeBillingTerm.TERM3;
            case ANNUAL -> FeeBillingTerm.ANNUAL;
            case MONTHLY -> FeeBillingTerm.MONTHLY;
        };
    }

    public static boolean matches(FeeBillingTerm billingTerm, TermType termType) {
        return billingTerm != null && billingTerm.equals(toBillingTerm(termType));
    }
}
