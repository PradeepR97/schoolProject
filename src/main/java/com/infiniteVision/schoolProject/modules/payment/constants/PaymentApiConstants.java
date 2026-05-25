package com.infiniteVision.schoolProject.modules.payment.constants;

import com.infiniteVision.schoolProject.constants.ApiConstants;

/**
 * Payment module API paths (reserved for future REST endpoints).
 */
public final class PaymentApiConstants {

    private PaymentApiConstants() {
    }

    public static final String PAYMENT_BASE = ApiConstants.API_V1_PREFIX + "/payments";
    public static final String LEDGERS_GENERATE = PAYMENT_BASE + "/ledgers/generate";
    public static final String INVOICES_GENERATE = PAYMENT_BASE + "/invoices/generate";
    public static final String COLLECT = PAYMENT_BASE + "/collect";
    public static final String DUES = PAYMENT_BASE + "/dues";
    public static final String STUDENT_DUES = PAYMENT_BASE + "/students/{studentId}/dues";
    public static final String RECEIPTS = PAYMENT_BASE + "/receipts";
}
