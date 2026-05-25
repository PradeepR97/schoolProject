package com.infiniteVision.schoolProject.modules.payment.receipt.dto.response;

import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptDetailResponseDTO {

    private Long paymentId;
    private String receiptNo;
    private LocalDate paymentDate;
    private PaymentRecordStatus status;
    private PaymentMode paymentMode;
    private BigDecimal amountPaid;
    private String transactionRef;
    private String chequeNo;
    private LocalDate chequeDate;
    private String bankName;
    private String remarks;

    private Long studentId;
    private String admissionNo;
    private String studentName;
    private String className;
    private String academicYear;
    private String term;
    private String feesPaymentStatus;

    private String invoiceNo;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private List<ReceiptFeeLineResponseDTO> feeBreakdown;
    private BigDecimal grossAmount;
    private BigDecimal discountAmount;
    private BigDecimal lateFee;
    private BigDecimal netAmount;
    private BigDecimal balanceAfterPayment;

    private String collectedBy;
    private String schoolName;
    private String schoolAddress;
    private String schoolPhone;

    private long totalPrintCount;
    private boolean reprintAllowed;
}
