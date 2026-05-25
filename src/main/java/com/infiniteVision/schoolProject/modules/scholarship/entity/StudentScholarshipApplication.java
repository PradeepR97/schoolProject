package com.infiniteVision.schoolProject.modules.scholarship.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Per-student request for a scholarship discount on fees. Full fee payment is always allowed;
 * discount applies on ledgers only when {@link ScholarshipApplicationStatus#APPROVED}.
 */
@Entity
@Table(
        name = "student_scholarship_applications",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_student_scholarship_app",
                    columnNames = {"student_id", "scheme_id", "academic_year_id"})
        },
        indexes = {
            @Index(name = "idx_ssa_student", columnList = "student_id"),
            @Index(name = "idx_ssa_status", columnList = "status"),
            @Index(name = "idx_ssa_year", columnList = "academic_year_id")
        })
@AttributeOverrides({
    @AttributeOverride(
            name = "id",
            column = @Column(name = "application_id", updatable = false, nullable = false)),
    @AttributeOverride(
            name = "createdAt",
            column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"student", "scheme", "academicYear"})
public class StudentScholarshipApplication extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ssa_student"))
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scheme_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ssa_scheme"))
    private SchoolScheme scheme;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "academic_year_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ssa_academic_year"))
    private AcademicYear academicYear;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ScholarshipApplicationStatus status = ScholarshipApplicationStatus.PENDING;

    @NotNull
    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "application_remarks", columnDefinition = "TEXT")
    private String applicationRemarks;

    @Column(name = "marks_at_application", precision = 6, scale = 2)
    private BigDecimal marksAtApplication;

    @Column(name = "requested_discount_percent", precision = 5, scale = 2)
    private BigDecimal requestedDiscountPercent;

    @Column(name = "approved_discount_percent", precision = 5, scale = 2)
    private BigDecimal approvedDiscountPercent;

    @Column(name = "principal_approved_by", length = 100)
    private String principalApprovedBy;

    @Column(name = "principal_approved_at")
    private LocalDateTime principalApprovedAt;

    @Column(name = "correspondent_approved_by", length = 100)
    private String correspondentApprovedBy;

    @Column(name = "correspondent_approved_at")
    private LocalDateTime correspondentApprovedAt;

    @Column(name = "rejected_by", length = 100)
    private String rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
}
