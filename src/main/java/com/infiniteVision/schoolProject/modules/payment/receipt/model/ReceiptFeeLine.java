package com.infiniteVision.schoolProject.modules.payment.receipt.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReceiptFeeLine {

    String label;
    BigDecimal amount;
}
