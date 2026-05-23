package com.infiniteVision.schoolProject.modules.payment.constants;

import com.infiniteVision.schoolProject.constants.ApiConstants;

/**
 * Payment module API paths (reserved for future REST endpoints).
 */
public final class PaymentApiConstants {

    private PaymentApiConstants() {
    }

    public static final String PAYMENT_BASE = ApiConstants.API_V1_PREFIX + "/payments";
    public static final String LEDGERS = PAYMENT_BASE + "/ledgers";
    public static final String INVOICES = PAYMENT_BASE + "/invoices";
    public static final String RECEIPTS = PAYMENT_BASE + "/receipts";
}
