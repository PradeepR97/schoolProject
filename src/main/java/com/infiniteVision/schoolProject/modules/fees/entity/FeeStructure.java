package com.infiniteVision.schoolProject.modules.fees.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
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
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Fee amount per academic year, grade, section, fee head, and term. Maps to {@code fee_structure}.
 */
@Entity
@Table(
        name = "fee_structure",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_fee_structure_year_class_section_head_term",
                        columnNames = {
                            "academic_year_id",
                            "class_id",
                            "section_id",
                            "fee_type_id",
                            "term_type"
                        })
        },
        indexes = {
                @Index(
                        name = "idx_fee_structure_year_class_section",
                        columnList = "academic_year_id, class_id, section_id"),
                @Index(name = "idx_fee_structure_active", columnList = "is_active")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "structure_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"academicYear", "classMaster", "section", "feeType"})
public class FeeStructure extends BaseEntity {

    @NotNull(message = "Academic year is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "academic_year_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_fee_structure_academic_year"))
    private AcademicYear academicYear;

    @NotNull(message = "Class is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "class_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_fee_structure_class"))
    private ClassMaster classMaster;

    @NotNull(message = "Section is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "section_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_fee_structure_section"))
    private SectionMaster section;

    @NotNull(message = "Fee type is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "fee_type_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_fee_structure_fee_type"))
    private FeeType feeType;

    @NotNull(message = "Term type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "term_type", nullable = false, length = 20)
    private TermType termType;

    @NotNull(message = "Amount is required")
    @PositiveOrZero(message = "Amount must be zero or positive")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @NotNull(message = "Late fee daily amount is required")
    @Column(name = "late_fee_daily", nullable = false, precision = 10, scale = 2)
    private BigDecimal lateFeeDaily = BigDecimal.ZERO;

    @NotNull(message = "Max late fee is required")
    @Column(name = "max_late_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxLateFee = BigDecimal.ZERO;

    @NotNull(message = "Scholarship allowed flag is required")
    @Column(name = "scholarship_allowed", nullable = false)
    private Boolean scholarshipAllowed = Boolean.TRUE;

    @NotNull(message = "Installment allowed flag is required")
    @Column(name = "installment_allowed", nullable = false)
    private Boolean installmentAllowed = Boolean.FALSE;

    @NotNull(message = "Active flag is required")
    @Column(name = "is_active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    /**
     * Soft-deletes this fee structure and marks it inactive.
     *
     * @param deletedByUserId ID of the authenticated user performing the delete
     */
    public void softDelete(Long deletedByUserId) {
        markDeleted(deletedByUserId);
        this.active = Boolean.FALSE;
    }
}
