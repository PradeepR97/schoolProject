package com.infiniteVision.schoolProject.modules.dashboard.repository.projection;

import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import java.math.BigDecimal;

/**
 * Lightweight payment row for dashboard recent payments.
 */
public interface RecentPaymentProjection {

    String getReceiptNo();

    String getStudentName();

    String getClassName();

    BigDecimal getAmount();

    PaymentMode getPaymentMethod();

    PaymentRecordStatus getPaymentStatus();
}
