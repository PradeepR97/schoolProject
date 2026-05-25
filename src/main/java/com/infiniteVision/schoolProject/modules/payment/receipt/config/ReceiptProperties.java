package com.infiniteVision.schoolProject.modules.payment.receipt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application configuration for receipt PDF storage and reprint limits.
 */
@ConfigurationProperties(prefix = "receipt")
@Getter
@Setter
public class ReceiptProperties {

    /** Directory where generated PDF receipts are stored. */
    private String storagePath = "./data/receipts";

    /** Maximum allowed reprints per payment (excluding the first print). */
    private int maxReprints = 3;

    /** When true, embed a QR code on laser PDF receipts. */
    private boolean qrEnabled = true;
}
