package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response after bulk fee collection across multiple fee-head ledgers.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCollectPaymentResponseDTO {

    private String paymentBatchId;
    private String batchReceiptNo;
    private Long studentId;
    private String admissionNo;
    private String studentName;
    private LocalDate paymentDate;
    private PaymentMode paymentMode;
    private BigDecimal totalAmountPaid;
    private int allocationCount;
    private FeesPaymentStatus studentFeesPaymentStatus;
    private List<BulkCollectPaymentLineResponseDTO> lines;
}
