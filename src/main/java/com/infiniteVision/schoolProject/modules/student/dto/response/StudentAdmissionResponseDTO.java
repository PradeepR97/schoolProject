package com.infiniteVision.schoolProject.modules.student.dto.response;

import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.enums.Medium;
import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Summary returned after successful student admission.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAdmissionResponseDTO {

    private Long studentId;
    private String admissionNo;
    private String firstName;
    private String lastName;
    private Medium medium;
    private Long classId;
    private Long academicYearId;
    private StudentStatus status;
    private FeesPaymentStatus feesPaymentStatus;
    private Long parentRecordId;
    private Long documentRecordId;
}
