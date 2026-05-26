package com.infiniteVision.schoolProject.modules.student.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.student.enums.BloodGroup;
import com.infiniteVision.schoolProject.modules.student.enums.Community;
import com.infiniteVision.schoolProject.modules.student.enums.Gender;
import com.infiniteVision.schoolProject.modules.student.enums.Medium;
import com.infiniteVision.schoolProject.modules.student.enums.Religion;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Master student profile. Maps to {@code students}.
 * <p>
 * Primary key and audit fields are inherited from {@link BaseEntity}.
 * {@code class_id} references grade ({@code class_master}); {@code section_id} references {@code section_master};
 * {@code academic_year_id} references {@code academic_year}.
 */
@Entity
@Table(
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_students_admission_no", columnNames = "admission_no"),
                @UniqueConstraint(name = "uk_students_aadhar_number", columnNames = "aadhar_number"),
                @UniqueConstraint(name = "uk_students_id_card_no", columnNames = "student_id_card_no")
        },
        indexes = {
                @Index(
                        name = "idx_students_class_section_year",
                        columnList = "class_id, section_id, academic_year_id"),
                @Index(name = "idx_students_status", columnList = "status"),
                @Index(name = "idx_students_admission_no", columnList = "admission_no"),
                @Index(name = "idx_students_fees_payment_status", columnList = "fees_payment_status")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "student_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"parents", "documents"})
public class Student extends BaseEntity {

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private StudentParent parents;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private StudentDocument documents;

    @NotBlank(message = "Admission number is required")
    @Size(max = 20, message = "Admission number must not exceed 20 characters")
    @Column(name = "admission_no", nullable = false, length = 20)
    private String admissionNo;

    /** Number printed on the physical student ID card (not the database primary key). */
    @Size(max = 20, message = "Student ID card number must not exceed 20 characters")
    @Column(name = "student_id_card_no", length = 20)
    private String studentIdCardNo;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be exactly 12 digits")
    @Column(name = "aadhar_number", length = 12)
    private String aadharNumber;

    @Size(max = 30, message = "EMIS number must not exceed 30 characters")
    @Column(name = "emis_number", length = 30)
    private String emisNumber;

    @Size(max = 30, message = "Ration card number must not exceed 30 characters")
    @Column(name = "ration_card_number", length = 30)
    private String rationCardNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    @Column(name = "nationality", length = 50)
    private String nationality;

    @Enumerated(EnumType.STRING)
    @Column(name = "medium", length = 20)
    private Medium medium;

    @Size(max = 50, message = "Mother tongue must not exceed 50 characters")
    @Column(name = "mother_tongue", length = 50)
    private String motherTongue;

    @Size(max = 30, message = "Study group must not exceed 30 characters")
    @Column(name = "study_group", length = 30)
    private String studyGroup;

    /** Class 10 / SSLC mark (percentage or score as stored). */
    @Column(name = "tenth_mark", precision = 6, scale = 2)
    private BigDecimal tenthMark;

    @Size(max = 255, message = "Identification mark 1 must not exceed 255 characters")
    @Column(name = "identification_mark_1", length = 255)
    private String identificationMark1;

    @Size(max = 255, message = "Identification mark 2 must not exceed 255 characters")
    @Column(name = "identification_mark_2", length = 255)
    private String identificationMark2;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @NotNull(message = "Class id is required")
    @Column(name = "class_id", nullable = false)
    private Long classId;

    @NotNull(message = "Section id is required")
    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    @NotNull(message = "Academic year id is required")
    @Column(name = "academic_year_id", nullable = false)
    private Long academicYearId;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", length = 20)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "religion", length = 20)
    private Religion religion;

    @Enumerated(EnumType.STRING)
    @Column(name = "community", length = 20)
    private Community community;

    @Column(name = "annual_income", precision = 12, scale = 2)
    private BigDecimal annualIncome;

    @NotNull(message = "Differently abled flag is required")
    @Column(name = "is_differently_abled", nullable = false)
    private Boolean differentlyAbled = Boolean.FALSE;

    @Size(max = 100, message = "Disability type must not exceed 100 characters")
    @Column(name = "disability_type", length = 100)
    private String disabilityType;

    @Column(name = "disability_percentage")
    private Integer disabilityPercentage;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StudentStatus status = StudentStatus.ACTIVE;

    @NotNull(message = "Fees payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "fees_payment_status", nullable = false, length = 20)
    private FeesPaymentStatus feesPaymentStatus = FeesPaymentStatus.PENDING;

    /** When false, transport fee structures are skipped during ledger generation. */
    @NotNull(message = "Transport required flag is required")
    @Column(name = "transport_required", nullable = false)
    private Boolean transportRequired = Boolean.FALSE;

    /**
     * Soft-deletes this student and sets {@link StudentStatus#DISCONTINUED}.
     *
     * @param deletedByUserId ID of the authenticated user performing the delete
     */
    public void softDelete(Long deletedByUserId) {
        markDeleted(deletedByUserId);
        this.status = StudentStatus.DISCONTINUED;
    }

    /** Ensures deleted students are always DISCONTINUED. */
    @PreUpdate
    private void enforceDiscontinuedWhenDeleted() {
        if (Boolean.TRUE.equals(getDeleted())) {
            this.status = StudentStatus.DISCONTINUED;
        }
    }
}
