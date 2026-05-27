package com.infiniteVision.schoolProject.modules.school.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.school.enums.AcademicYearStartMonth;
import com.infiniteVision.schoolProject.modules.school.enums.AffiliationBoard;
import com.infiniteVision.schoolProject.modules.school.enums.LateFeeType;
import com.infiniteVision.schoolProject.modules.school.enums.SchoolCategory;
import com.infiniteVision.schoolProject.modules.school.enums.SchoolType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * School master profile and configuration for academics, branding, billing, and contact details.
 */
@Entity
@Table(
        name = "schools",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_school_code", columnNames = "school_code"),
                @UniqueConstraint(name = "uq_udise_code", columnNames = "udise_code"),
                @UniqueConstraint(name = "uq_affiliation", columnNames = "affiliation_no")
        },
        indexes = {
                @Index(name = "idx_schools_active", columnList = "is_active"),
                @Index(name = "idx_schools_deleted", columnList = "deleted")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "school_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SchoolDetails extends BaseEntity {

    @NotBlank(message = "School name is required")
    @Column(name = "school_name", nullable = false, length = 200)
    private String schoolName;

    @NotBlank(message = "School code is required")
    @Column(name = "school_code", nullable = false, length = 30)
    private String schoolCode;

    @NotNull(message = "School type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "school_type", nullable = false, length = 30)
    private SchoolType schoolType;

    @NotNull(message = "School category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "school_category", nullable = false, length = 30)
    private SchoolCategory schoolCategory;

    /** CSV encoded values (e.g. {@code TAMIL,ENGLISH}) mapped from API enum list. */
    @NotBlank(message = "Medium of instruction is required")
    @Column(name = "medium_of_instruction", nullable = false, length = 120)
    private String mediumOfInstruction;

    @Column(name = "established_year")
    private Integer establishedYear;

    @Column(name = "affiliation_no", length = 50)
    private String affiliationNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "affiliation_board", length = 30)
    private AffiliationBoard affiliationBoard;

    @Column(name = "udise_code", length = 20)
    private String udiseCode;

    @Column(name = "trust_name", length = 200)
    private String trustName;

    @Column(name = "trust_reg_no", length = 50)
    private String trustRegNo;

    @NotBlank(message = "Primary phone is required")
    @Column(name = "phone_primary", nullable = false, length = 15)
    private String phonePrimary;

    @Column(name = "phone_secondary", length = 15)
    private String phoneSecondary;

    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "fax", length = 20)
    private String fax;

    @NotBlank(message = "Address line 1 is required")
    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank(message = "District is required")
    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @NotBlank(message = "State is required")
    @Column(name = "state", nullable = false, length = 100)
    private String state = "Tamil Nadu";

    @NotBlank(message = "Pincode is required")
    @Column(name = "pincode", nullable = false, length = 10)
    private String pincode;

    @NotBlank(message = "Country is required")
    @Column(name = "country", nullable = false, length = 50)
    private String country = "India";

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "principal_name", length = 150)
    private String principalName;

    @Column(name = "principal_phone", length = 15)
    private String principalPhone;

    @Column(name = "principal_email", length = 150)
    private String principalEmail;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    @Column(name = "signature_url", length = 500)
    private String signatureUrl;

    @Column(name = "school_motto", length = 255)
    private String schoolMotto;

    @Column(name = "school_color_primary", length = 7)
    private String schoolColorPrimary;

    @Column(name = "school_color_secondary", length = 7)
    private String schoolColorSecondary;

    @NotNull(message = "Academic year start month is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "academic_year_start", nullable = false, length = 10)
    private AcademicYearStartMonth academicYearStart = AcademicYearStartMonth.JUNE;

    @NotNull(message = "Working days per week is required")
    @Column(name = "working_days_per_week", nullable = false)
    private Integer workingDaysPerWeek = 6;

    @Column(name = "total_classes")
    private Integer totalClasses;

    @NotBlank(message = "Invoice prefix is required")
    @Column(name = "invoice_prefix", nullable = false, length = 10)
    private String invoicePrefix = "INV";

    @NotBlank(message = "Receipt prefix is required")
    @Column(name = "receipt_prefix", nullable = false, length = 10)
    private String receiptPrefix = "RCP";

    @NotBlank(message = "Currency is required")
    @Column(name = "currency", nullable = false, length = 5)
    private String currency = "INR";

    @NotNull(message = "Late fee applicable flag is required")
    @Column(name = "late_fee_applicable", nullable = false)
    private Boolean lateFeeApplicable = Boolean.FALSE;

    @Column(name = "late_fee_amount", precision = 10, scale = 2)
    private BigDecimal lateFeeAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "late_fee_type", length = 20)
    private LateFeeType lateFeeType;

    @NotNull(message = "Grace period days is required")
    @Column(name = "grace_period_days", nullable = false)
    private Integer gracePeriodDays = 0;

    @NotNull(message = "Active flag is required")
    @Column(name = "is_active", nullable = false)
    private Boolean active = Boolean.TRUE;

    /**
     * Soft-delete school and mark inactive.
     *
     * @param deletedByUserId authenticated user id
     */
    public void softDelete(Long deletedByUserId) {
        markDeleted(deletedByUserId);
        this.active = Boolean.FALSE;
    }
}
