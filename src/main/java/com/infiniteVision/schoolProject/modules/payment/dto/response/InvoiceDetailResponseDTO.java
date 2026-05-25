package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Full invoice detail for GET-by-id APIs.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailResponseDTO {

    private Long invoiceId;
    private String invoiceNo;
    private Long studentId;
    private String admissionNo;
    private String studentName;
    private Long ledgerId;
    private Long academicYearId;
    private FeeBillingTerm term;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal grossAmount;
    private BigDecimal discountAmount;
    private BigDecimal lateFee;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private InvoiceStatus status;
}
