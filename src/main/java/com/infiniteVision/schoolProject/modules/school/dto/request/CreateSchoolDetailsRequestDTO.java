package com.infiniteVision.schoolProject.modules.school.dto.request;

import com.infiniteVision.schoolProject.modules.school.enums.AcademicYearStartMonth;
import com.infiniteVision.schoolProject.modules.school.enums.AffiliationBoard;
import com.infiniteVision.schoolProject.modules.school.enums.LateFeeType;
import com.infiniteVision.schoolProject.modules.school.enums.MediumOfInstruction;
import com.infiniteVision.schoolProject.modules.school.enums.SchoolCategory;
import com.infiniteVision.schoolProject.modules.school.enums.SchoolType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body to create school details.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSchoolDetailsRequestDTO {

    @NotBlank(message = "School name is required")
    @Size(max = 200, message = "School name must not exceed 200 characters")
    private String schoolName;

    @NotBlank(message = "School code is required")
    @Size(max = 30, message = "School code must not exceed 30 characters")
    private String schoolCode;

    @NotNull(message = "School type is required")
    private SchoolType schoolType;

    @NotNull(message = "School category is required")
    private SchoolCategory schoolCategory;

    @NotEmpty(message = "At least one medium of instruction is required")
    private List<MediumOfInstruction> mediumOfInstruction;

    private Integer establishedYear;
    private String affiliationNo;
    private AffiliationBoard affiliationBoard;
    private String udiseCode;
    private String trustName;
    private String trustRegNo;

    @NotBlank(message = "Primary phone is required")
    @Size(max = 15, message = "Primary phone must not exceed 15 characters")
    private String phonePrimary;

    private String phoneSecondary;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    private String website;
    private String fax;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;
    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "District is required")
    private String district;

    private String state;

    @NotBlank(message = "Pincode is required")
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
