package com.infiniteVision.schoolProject.modules.student.dto.response;

import com.infiniteVision.schoolProject.modules.student.enums.PrimaryContact;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentParentsResponseDTO {

    private Long parentRecordId;
    private String fatherName;
    private String fatherPhone;
    private String fatherEmail;
    private String fatherOccupation;
    private BigDecimal fatherAnnualIncome;
    private String fatherQualification;
    private String motherName;
    private String motherPhone;
    private String motherEmail;
    private String motherOccupation;
    private BigDecimal motherAnnualIncome;
    private String motherQualification;
    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
    private String guardianOccupation;
    private String guardianQualification;
    private String guardianRelationship;
    private PrimaryContact primaryContact;
}
