package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Aggregated outstanding fees per student for the due-payments list screen.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDueSummaryResponseDTO {

    private Long studentId;
    private String admissionNo;
    private String studentName;
    private Long classId;
    private Long academicYearId;
    private FeesPaymentStatus feesPaymentStatus;
    private BigDecimal totalBalanceDue;
    private BigDecimal totalNetAmount;
    private BigDecimal totalPaidAmount;
    private long dueLedgerCount;
    private long overdueLedgerCount;
}
