package com.infiniteVision.schoolProject.modules.payment.receipt.entity;

import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrintAction;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrinterType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.ReceiptCopyType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.RenderedFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Audit trail for receipt print and reprint actions. Maps to {@code payment_receipt_print_log}.
 */
@Entity
@Table(name = "payment_receipt_print_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptPrintLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "receipt_no", nullable = false, length = 20)
    private String receiptNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "printer_type", nullable = false, length = 30)
    private PrinterType printerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "copy_type", nullable = false, length = 20)
    private ReceiptCopyType copyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "print_action", nullable = false, length = 20)
    private PrintAction printAction;

    @Column(name = "print_count", nullable = false)
    private Integer printCount;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "rendered_format", nullable = false, length = 20)
    private RenderedFormat renderedFormat;

    @Column(name = "watermark_applied", nullable = false)
    private Boolean watermarkApplied = Boolean.FALSE;

    @Column(name = "printed_by", nullable = false, length = 100)
    private String printedBy;

    @Column(name = "printed_at", nullable = false)
    private LocalDateTime printedAt;

    @Column(name = "remarks", length = 500)
    private String remarks;
}
