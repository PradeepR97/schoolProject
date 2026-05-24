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
 * Invoice summary for dues and receipts.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceSummaryResponseDTO {

    private Long invoiceId;
    private String invoiceNo;
    private FeeBillingTerm term;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private InvoiceStatus status;
}
