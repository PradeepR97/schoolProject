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
}
