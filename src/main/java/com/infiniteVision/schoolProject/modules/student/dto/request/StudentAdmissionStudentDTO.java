package com.infiniteVision.schoolProject.modules.student.dto.request;

import com.infiniteVision.schoolProject.modules.student.enums.BloodGroup;
import com.infiniteVision.schoolProject.modules.student.enums.Community;
import com.infiniteVision.schoolProject.modules.student.enums.Gender;
import com.infiniteVision.schoolProject.modules.student.enums.Religion;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * Student profile section of an admission request.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAdmissionStudentDTO {

    @NotBlank(message = "Admission number is required")
    @Size(max = 20, message = "Admission number must not exceed 20 characters")
    private String admissionNo;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be exactly 12 digits")
    private String aadharNumber;

    @Size(max = 30, message = "EMIS number must not exceed 30 characters")
    private String emisNumber;

    @Size(max = 30, message = "Ration card number must not exceed 30 characters")
    private String rationCardNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;

    private String address;

    @NotNull(message = "Class id is required")
    private Long classId;

    @NotNull(message = "Academic year id is required")
    private Long academicYearId;

    private BloodGroup bloodGroup;

    private Religion religion;

    private Community community;

    private BigDecimal annualIncome;

    private StudentStatus status;

    private FeesPaymentStatus feesPaymentStatus;
}
