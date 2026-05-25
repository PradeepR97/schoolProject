package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * One fee-head line in a bulk payment collection response.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCollectPaymentLineResponseDTO {

    private Long paymentId;
    private String receiptNo;
    private Long ledgerId;
    private Long invoiceId;
    private String feeHeadName;
    private String feeHeadCode;
    private BigDecimal amountPaid;
    private BigDecimal ledgerBalanceAmount;
    private LedgerStatus ledgerStatus;
    private PaymentRecordStatus status;
}
