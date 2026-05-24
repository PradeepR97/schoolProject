package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response after collecting a fee payment.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptResponseDTO {

    private Long paymentId;
    private String receiptNo;
    private Long studentId;
    private Long ledgerId;
    private Long invoiceId;
    private BigDecimal amountPaid;
    private PaymentMode paymentMode;
    private LocalDate paymentDate;
    private PaymentRecordStatus status;
    private BigDecimal ledgerPaidAmount;
    private BigDecimal ledgerBalanceAmount;
    private LedgerStatus ledgerStatus;
    private InvoiceStatus invoiceStatus;
    private FeesPaymentStatus studentFeesPaymentStatus;
}
