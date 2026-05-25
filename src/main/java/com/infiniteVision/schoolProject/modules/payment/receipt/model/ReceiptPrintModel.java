package com.infiniteVision.schoolProject.modules.payment.receipt.model;

import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.ReceiptCopyType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

/**
 * Aggregated view model used by all receipt renderers (PDF and plain text).
 */
@Value
@Builder
public class ReceiptPrintModel {

    Long paymentId;
    String receiptNo;
    LocalDate paymentDate;
    PaymentRecordStatus paymentStatus;
    PaymentMode paymentMode;
    String transactionRef;
    String chequeNo;
    LocalDate chequeDate;
    String bankName;
    String paymentRemarks;

    Long studentId;
    String admissionNo;
    String studentName;
    String className;
    String academicYearLabel;
    String termLabel;

    String invoiceNo;
    LocalDate invoiceDate;
    LocalDate dueDate;

    List<ReceiptFeeLine> feeLines;
    BigDecimal grossAmount;
    BigDecimal discountAmount;
    BigDecimal lateFee;
    BigDecimal netAmount;
    BigDecimal amountPaid;
    BigDecimal balanceAfterPayment;

    String collectedByName;
    String feesPaymentStatus;

    String schoolName;
    String schoolAddress;
    String schoolPhone;
    String schoolEmail;
    String logoPath;
    String footerMessage;
    String signatureLabel1;
    String signatureLabel2;

    ReceiptCopyType copyType;
    boolean reprint;
    int printSequence;
    String qrPayload;

    LocalDateTime generatedAt;
}
