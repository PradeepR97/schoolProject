package com.infiniteVision.schoolProject.modules.student.dto.request;

import com.infiniteVision.schoolProject.modules.student.enums.PrimaryContact;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Optional parent/guardian fields for partial update.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentParentsDTO {

    @Size(max = 150, message = "Father name must not exceed 150 characters")
    private String fatherName;

    @Size(max = 15, message = "Father phone must not exceed 15 characters")
    private String fatherPhone;

    @Email(message = "Father email must be a valid address")
    @Size(max = 100, message = "Father email must not exceed 100 characters")
    private String fatherEmail;

    @Size(max = 100, message = "Father occupation must not exceed 100 characters")
    private String fatherOccupation;

    private BigDecimal fatherAnnualIncome;

    @Size(max = 150, message = "Father qualification must not exceed 150 characters")
    private String fatherQualification;

    @Size(max = 150, message = "Mother name must not exceed 150 characters")
    private String motherName;

    @Size(max = 15, message = "Mother phone must not exceed 15 characters")
    private String motherPhone;

    @Email(message = "Mother email must be a valid address")
    @Size(max = 100, message = "Mother email must not exceed 100 characters")
    private String motherEmail;

    @Size(max = 100, message = "Mother occupation must not exceed 100 characters")
    private String motherOccupation;

    private BigDecimal motherAnnualIncome;

    @Size(max = 150, message = "Mother qualification must not exceed 150 characters")
    private String motherQualification;

    @Size(max = 150, message = "Guardian name must not exceed 150 characters")
    private String guardianName;

    @Size(max = 15, message = "Guardian phone must not exceed 15 characters")
    private String guardianPhone;

    @Email(message = "Guardian email must be a valid address")
    @Size(max = 100, message = "Guardian email must not exceed 100 characters")
    private String guardianEmail;

    @Size(max = 100, message = "Guardian occupation must not exceed 100 characters")
    private String guardianOccupation;

    @Size(max = 150, message = "Guardian qualification must not exceed 150 characters")
    private String guardianQualification;

    @Size(max = 50, message = "Guardian relationship must not exceed 50 characters")
    private String guardianRelationship;

    private PrimaryContact primaryContact;
}
