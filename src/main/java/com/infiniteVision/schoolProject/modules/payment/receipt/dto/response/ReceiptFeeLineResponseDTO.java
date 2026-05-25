package com.infiniteVision.schoolProject.modules.payment.receipt.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptFeeLineResponseDTO {

    private String label;
    private BigDecimal amount;
}
