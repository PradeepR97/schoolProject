package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * One ledger line on the student dues screen.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeDueItemResponseDTO {

    private Long ledgerId;
    private Long structureId;
    private String feeHeadName;
    private FeeBillingTerm term;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private LocalDate dueDate;
    private LedgerStatus status;
    private Long invoiceId;
    private String invoiceNo;
    private InvoiceStatus invoiceStatus;
}
