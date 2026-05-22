package com.infiniteVision.schoolProject.modules.student.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.student.enums.DocumentVerificationStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Uploaded document metadata and file URLs for a single student.
 * Maps to {@code student_documents}. One row per student ({@code student_id} unique).
 */
@Entity
@Table(
        name = "student_documents",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_doc_student", columnNames = "student_id"),
                @UniqueConstraint(name = "uq_aadhar_no", columnNames = "aadhar_no")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "doc_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "student")
public class StudentDocument extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_doc_student"))
    private Student student;

    @Size(max = 500, message = "Profile photo URL must not exceed 500 characters")
    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Column(name = "profile_photo_updated_at")
    private LocalDateTime profilePhotoUpdatedAt;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be exactly 12 digits")
    @Column(name = "aadhar_no", length = 12)
    private String aadharNo;

    @Size(max = 500, message = "Aadhar file URL must not exceed 500 characters")
    @Column(name = "aadhar_file_url", length = 500)
    private String aadharFileUrl;

    @NotNull(message = "Birth certificate available flag is required")
    @Column(name = "birth_cert_available", nullable = false)
    private Boolean birthCertAvailable = Boolean.FALSE;

    @Size(max = 500, message = "Birth certificate URL must not exceed 500 characters")
    @Column(name = "birth_certificate_url", length = 500)
    private String birthCertificateUrl;

    @Size(max = 30, message = "Community certificate number must not exceed 30 characters")
    @Column(name = "community_cert_no", length = 30)
    private String communityCertNo;

    @Column(name = "community_cert_date")
    private LocalDate communityCertDate;

    @Size(max = 500, message = "Community certificate URL must not exceed 500 characters")
    @Column(name = "community_certificate_url", length = 500)
    private String communityCertificateUrl;

    @Size(max = 30, message = "Income certificate number must not exceed 30 characters")
    @Column(name = "income_cert_no", length = 30)
    private String incomeCertNo;

    @Column(name = "income_cert_date")
    private LocalDate incomeCertDate;

    @Column(name = "annual_income", precision = 12, scale = 2)
    private BigDecimal annualIncome;

    @Size(max = 500, message = "Income certificate URL must not exceed 500 characters")
    @Column(name = "income_certificate_url", length = 500)
    private String incomeCertificateUrl;

    @NotNull(message = "Special child flag is required")
    @Column(name = "special_child", nullable = false)
    private Boolean specialChild = Boolean.FALSE;

    @Size(max = 100, message = "Disability type must not exceed 100 characters")
    @Column(name = "disability_type", length = 100)
    private String disabilityType;

    @Column(name = "disability_percentage", precision = 5, scale = 2)
    private BigDecimal disabilityPercentage;

    @Size(max = 50, message = "Special child certificate number must not exceed 50 characters")
    @Column(name = "special_child_cert_no", length = 50)
    private String specialChildCertNo;

    @Size(max = 500, message = "Special child certificate URL must not exceed 500 characters")
    @Column(name = "special_child_cert_url", length = 500)
    private String specialChildCertUrl;

    @Size(max = 30, message = "TC number must not exceed 30 characters")
    @Column(name = "tc_number", length = 30)
    private String tcNumber;

    @Column(name = "tc_date")
    private LocalDate tcDate;

    @Size(max = 200, message = "Previous school name must not exceed 200 characters")
    @Column(name = "prev_school_name", length = 200)
    private String prevSchoolName;

    @Size(max = 500, message = "TC file URL must not exceed 500 characters")
    @Column(name = "tc_file_url", length = 500)
    private String tcFileUrl;

    @Pattern(regexp = "^\\d{12}$", message = "Parent Aadhar number must be exactly 12 digits")
    @Column(name = "parent_aadhar_no", length = 12)
    private String parentAadharNo;

    @Size(max = 500, message = "Parent Aadhar file URL must not exceed 500 characters")
    @Column(name = "parent_aadhar_file_url", length = 500)
    private String parentAadharFileUrl;

    @NotNull(message = "Verification status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    private DocumentVerificationStatus verificationStatus = DocumentVerificationStatus.UNVERIFIED;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(
            name = "uploaded_by",
            foreignKey = @ForeignKey(name = "fk_doc_uploaded_by"))
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(
            name = "verified_by",
            foreignKey = @ForeignKey(name = "fk_doc_verified_by"))
    private User verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}
