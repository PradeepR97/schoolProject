package com.infiniteVision.schoolProject.modules.payment.receipt.entity;

import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrinterType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * School branding and default printer settings for receipts. Maps to {@code school_print_settings}.
 */
@Entity
@Table(name = "school_print_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolPrintSettings {

    @Id
    @Column(name = "setting_id", nullable = false)
    private Long id;

    @Column(name = "school_name", nullable = false, length = 200)
    private String schoolName;

    @Column(name = "school_address", length = 500)
    private String schoolAddress;

    @Column(name = "school_phone", length = 30)
    private String schoolPhone;

    @Column(name = "school_email", length = 100)
    private String schoolEmail;

    @Column(name = "logo_path", length = 500)
    private String logoPath;

    @Column(name = "footer_message", length = 500)
    private String footerMessage;

    @Column(name = "signature_label_1", length = 100)
    private String signatureLabel1;

    @Column(name = "signature_label_2", length = 100)
    private String signatureLabel2;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_printer_type", nullable = false, length = 30)
    private PrinterType defaultPrinterType = PrinterType.LASER;

    @Column(name = "thermal_width_mm", nullable = false)
    private Integer thermalWidthMm = 80;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
