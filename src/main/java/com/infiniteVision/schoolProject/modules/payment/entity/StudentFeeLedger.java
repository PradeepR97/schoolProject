package com.infiniteVision.schoolProject.modules.payment.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
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
 * Per-student fee obligation for a structure and term. Maps to {@code student_fee_ledger}.
 */
@Entity
@Table(
        name = "student_fee_ledger",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_ledger",
                        columnNames = {"student_id", "academic_year_id", "term", "structure_id"})
        },
        indexes = {
                @Index(name = "idx_ledger_student", columnList = "student_id"),
                @Index(name = "idx_ledger_status", columnList = "status"),
                @Index(name = "idx_ledger_due_date", columnList = "due_date")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "ledger_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"student", "feeStructure", "academicYear"})
public class StudentFeeLedger extends BaseEntity {

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ledger_student"))
    private Student student;

    @NotNull(message = "Fee structure is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "structure_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ledger_structure"))
    private FeeStructure feeStructure;

    @NotNull(message = "Academic year is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "academic_year_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ledger_academic_year"))
    private AcademicYear academicYear;

    @NotNull(message = "Term is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "term", nullable = false, length = 20)
    private FeeBillingTerm term;

    @NotNull(message = "Actual amount is required")
    @Column(name = "actual_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualAmount = BigDecimal.ZERO;

    @NotNull(message = "Discount amount is required")
    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotNull(message = "Late fee is required")
    @Column(name = "late_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal lateFee = BigDecimal.ZERO;

    @NotNull(message = "Net amount is required")
    @Column(name = "net_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal netAmount = BigDecimal.ZERO;

    @NotNull(message = "Paid amount is required")
    @Column(name = "paid_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @NotNull(message = "Balance amount is required")
    @Column(name = "balance_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LedgerStatus status = LedgerStatus.PENDING;
}
