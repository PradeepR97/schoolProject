package com.infiniteVision.schoolProject.modules.student.dto.request;

import com.infiniteVision.schoolProject.modules.student.enums.BloodGroup;
import com.infiniteVision.schoolProject.modules.student.enums.Community;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.enums.Gender;
import com.infiniteVision.schoolProject.modules.student.enums.Medium;
import com.infiniteVision.schoolProject.modules.student.enums.Religion;
import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Optional student profile fields for partial update.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentProfileDTO {

    @Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be exactly 12 digits")
    private String aadharNumber;

    @Size(max = 30, message = "EMIS number must not exceed 30 characters")
    private String emisNumber;

    @Size(max = 30, message = "Ration card number must not exceed 30 characters")
    private String rationCardNumber;

    @Size(max = 30, message = "Application number must not exceed 30 characters")
    private String applicationNumber;

    @Size(max = 20, message = "Student ID card number must not exceed 20 characters")
    private String studentIdCardNo;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;

    private Medium medium;

    @Size(max = 50, message = "Mother tongue must not exceed 50 characters")
    private String motherTongue;

    @Size(max = 30, message = "Study group must not exceed 30 characters")
    private String studyGroup;

    private BigDecimal tenthMark;

    @Size(max = 255, message = "Identification mark 1 must not exceed 255 characters")
    private String identificationMark1;

    @Size(max = 255, message = "Identification mark 2 must not exceed 255 characters")
    private String identificationMark2;

    private String address;

    private Long classId;

    private Long academicYearId;

    private BloodGroup bloodGroup;

    private Religion religion;

    private Community community;

    private BigDecimal annualIncome;

    private Boolean differentlyAbled;

    @Size(max = 100, message = "Disability type must not exceed 100 characters")
    private String disabilityType;

    private Integer disabilityPercentage;

    private StudentStatus status;

    private FeesPaymentStatus feesPaymentStatus;
}
