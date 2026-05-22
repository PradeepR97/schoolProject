package com.infiniteVision.schoolProject.modules.student.dto.response;

import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Summary row for paginated student list screens.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentListItemResponseDTO {

    private Long studentId;
    private String studentIdCardNo;
    private String studentName;
    private String className;
    private String parentName;
    private String parentPhone;
    private FeesPaymentStatus feesPaymentStatus;
}
