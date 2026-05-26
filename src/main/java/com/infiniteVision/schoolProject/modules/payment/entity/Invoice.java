package com.infiniteVision.schoolProject.modules.payment.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
 * Fee invoice for a student ledger row. Maps to {@code invoices}.
 */
@Entity
@Table(
        name = "invoices",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_invoice_no", columnNames = "invoice_no"),
                @UniqueConstraint(name = "uq_invoice_ledger", columnNames = "ledger_id")
        },
        indexes = {
                @Index(name = "idx_invoice_student", columnList = "student_id"),
                @Index(name = "idx_invoice_status", columnList = "status"),
                @Index(name = "idx_invoice_date", columnList = "invoice_date")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "invoice_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(
        callSuper = true,
        exclude = {"student", "classMaster", "section", "academicYear", "ledger", "generatedByUser"})
public class Invoice extends BaseEntity {

    @NotBlank(message = "Invoice number is required")
    @Size(max = 20, message = "Invoice number must not exceed 20 characters")
    @Column(name = "invoice_no", nullable = false, length = 20)
    private String invoiceNo;

    @NotNull(message = "Invoice date is required")
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_invoice_student"))
    private Student student;

    @NotNull(message = "Class is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "class_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_invoice_class"))
    private ClassMaster classMaster;

    @NotNull(message = "Section is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "section_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_invoice_section"))
    private SectionMaster section;

    @NotNull(message = "Academic year is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "academic_year_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_invoice_academic_year"))
    private AcademicYear academicYear;

    @NotNull(message = "Ledger is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "ledger_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_invoice_ledger"))
    private StudentFeeLedger ledger;

    @NotNull(message = "Term is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "term", nullable = false, length = 20)
    private FeeBillingTerm term;

    @NotNull(message = "Tuition fee is required")
    @Column(name = "tuition_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal tuitionFee = BigDecimal.ZERO;

    @NotNull(message = "Exam fee is required")
    @Column(name = "exam_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal examFee = BigDecimal.ZERO;

    @NotNull(message = "Lab fee is required")
    @Column(name = "lab_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal labFee = BigDecimal.ZERO;

    @NotNull(message = "Library fee is required")
    @Column(name = "library_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal libraryFee = BigDecimal.ZERO;

    @NotNull(message = "Sports fee is required")
    @Column(name = "sports_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal sportsFee = BigDecimal.ZERO;

    @NotNull(message = "Transport fee is required")
    @Column(name = "transport_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal transportFee = BigDecimal.ZERO;

    @NotNull(message = "Uniform fee is required")
    @Column(name = "uniform_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal uniformFee = BigDecimal.ZERO;

    @NotNull(message = "Misc fee is required")
    @Column(name = "misc_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal miscFee = BigDecimal.ZERO;

    @NotNull(message = "Gross amount is required")
    @Column(name = "gross_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossAmount = BigDecimal.ZERO;

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

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(
            name = "generated_by",
            foreignKey = @ForeignKey(name = "fk_invoice_generated_by"))
    private User generatedByUser;
}
