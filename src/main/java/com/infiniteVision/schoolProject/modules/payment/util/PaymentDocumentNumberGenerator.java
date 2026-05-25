package com.infiniteVision.schoolProject.modules.payment.util;

import java.time.Year;

/**
 * Generates unique invoice and receipt numbers for the payment module.
 */
public final class PaymentDocumentNumberGenerator {

    private PaymentDocumentNumberGenerator() {
    }

    public static String nextInvoiceNumber(long sequence) {
        return String.format("INV-%d-%05d", Year.now().getValue(), sequence);
    }

    public static String nextReceiptNumber(long sequence) {
        return String.format("RCP-%d-%05d", Year.now().getValue(), sequence);
    }

    /** Line receipt under a bulk batch, e.g. RCP-2026-00045-01 (max 20 chars). */
    public static String batchLineReceiptNumber(String batchReceiptNo, int lineIndex) {
        String suffix = String.format("-%02d", lineIndex);
        int maxBase = 20 - suffix.length();
        if (batchReceiptNo.length() <= maxBase) {
            return batchReceiptNo + suffix;
        }
        return batchReceiptNo.substring(0, maxBase) + suffix;
    }
}
