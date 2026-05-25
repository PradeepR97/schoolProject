package com.infiniteVision.schoolProject.modules.dashboard.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Latest fee payment row for the dashboard widget.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentPaymentResponseDTO {

    private String receiptNo;
    private String studentName;
    private String className;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
}
