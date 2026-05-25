package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Full fee ledger detail for GET-by-id APIs.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeLedgerDetailResponseDTO {

    private Long ledgerId;
    private Long studentId;
    private Long academicYearId;
    private Long structureId;
    private String feeHeadName;
    private FeeBillingTerm term;
    private BigDecimal actualAmount;
    private BigDecimal discountAmount;
    private BigDecimal lateFee;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private LocalDate dueDate;
    private LedgerStatus status;
    private BigDecimal pendingScholarshipDiscount;
    private ScholarshipApplicationStatus pendingScholarshipStatus;
}
