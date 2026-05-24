package com.infiniteVision.schoolProject.modules.payment.dto.response;

import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Outstanding and partial fee rows for a student.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeDuesResponseDTO {

    private Long studentId;
    private String admissionNo;
    private String studentName;
    private Long academicYearId;
    private FeesPaymentStatus feesPaymentStatus;
    private BigDecimal totalPendingScholarshipDiscount;
    private List<StudentFeeDueItemResponseDTO> ledgers;
}
