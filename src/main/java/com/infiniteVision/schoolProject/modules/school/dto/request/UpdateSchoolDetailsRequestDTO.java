package com.infiniteVision.schoolProject.modules.school.dto.request;

import com.infiniteVision.schoolProject.modules.school.enums.AcademicYearStartMonth;
import com.infiniteVision.schoolProject.modules.school.enums.AffiliationBoard;
import com.infiniteVision.schoolProject.modules.school.enums.LateFeeType;
import com.infiniteVision.schoolProject.modules.school.enums.MediumOfInstruction;
import com.infiniteVision.schoolProject.modules.school.enums.SchoolCategory;
import com.infiniteVision.schoolProject.modules.school.enums.SchoolType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Partial update body for school details.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSchoolDetailsRequestDTO {

    @Size(max = 200, message = "School name must not exceed 200 characters")
    private String schoolName;

    @Size(max = 30, message = "School code must not exceed 30 characters")
    private String schoolCode;

    private SchoolType schoolType;
    private SchoolCategory schoolCategory;
    private List<MediumOfInstruction> mediumOfInstruction;
    private Integer establishedYear;
    private String affiliationNo;
    private AffiliationBoard affiliationBoard;
    private String udiseCode;
    private String trustName;
    private String trustRegNo;

    @Size(max = 15, message = "Primary phone must not exceed 15 characters")
    private String phonePrimary;

    private String phoneSecondary;

    @Email(message = "Email must be a valid address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    private String website;
    private String fax;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String state;
    private String pincode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String principalName;
    private String principalPhone;

    @Email(message = "Principal email must be a valid address")
    private String principalEmail;

    private String logoUrl;
    private String bannerUrl;
    private String signatureUrl;
    private String schoolMotto;
    private String schoolColorPrimary;
    private String schoolColorSecondary;
    private AcademicYearStartMonth academicYearStart;
    private Integer workingDaysPerWeek;
    private Integer totalClasses;
    private String invoicePrefix;
    private String receiptPrefix;
    private String currency;
    private Boolean lateFeeApplicable;
    private BigDecimal lateFeeAmount;
    private LateFeeType lateFeeType;
    private Integer gracePeriodDays;
    private Boolean active;
}
