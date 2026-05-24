package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Payment row for paginated list APIs.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentListItemResponseDTO {

    private Long paymentId;
    private String receiptNo;
    private Long studentId;
    private String admissionNo;
    private String studentName;
    private Long ledgerId;
    private Long invoiceId;
    private String invoiceNo;
    private BigDecimal amountPaid;
    private PaymentMode paymentMode;
    private LocalDate paymentDate;
    private PaymentRecordStatus status;
    private String collectedByUsername;
}
