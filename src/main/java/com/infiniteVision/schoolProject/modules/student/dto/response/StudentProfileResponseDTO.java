package com.infiniteVision.schoolProject.modules.student.dto.response;

import com.infiniteVision.schoolProject.modules.student.enums.BloodGroup;
import com.infiniteVision.schoolProject.modules.student.enums.Community;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.enums.Gender;
import com.infiniteVision.schoolProject.modules.student.enums.Religion;
import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponseDTO {

    private Long studentId;
    private String admissionNo;
    private String applicationNumber;
    private String studentIdCardNo;
    private String aadharNumber;
    private String emisNumber;
    private String rationCardNumber;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String nationality;
    private String motherTongue;
    private String studyGroup;
    private BigDecimal tenthMark;
    private String identificationMark1;
    private String identificationMark2;
    private String address;
    private Long classId;
    private String className;
    private Long academicYearId;
    private BloodGroup bloodGroup;
    private Religion religion;
    private Community community;
    private BigDecimal annualIncome;
    private Boolean differentlyAbled;
    private String disabilityType;
    private Integer disabilityPercentage;
    private StudentStatus status;
    private FeesPaymentStatus feesPaymentStatus;
}
