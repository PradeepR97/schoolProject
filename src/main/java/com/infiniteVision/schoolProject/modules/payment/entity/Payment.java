package com.infiniteVision.schoolProject.modules.payment.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
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
 * Fee payment receipt linked to ledger and invoice. Maps to {@code payments}.
 */
@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_receipt_no", columnNames = "receipt_no")
        },
        indexes = {
                @Index(name = "idx_payment_student", columnList = "student_id"),
                @Index(name = "idx_payment_date", columnList = "payment_date"),
                @Index(name = "idx_payment_status", columnList = "status")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "payment_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"student", "ledger", "invoice", "collectedByUser"})
public class Payment extends BaseEntity {

    @NotBlank(message = "Receipt number is required")
    @Size(max = 20, message = "Receipt number must not exceed 20 characters")
    @Column(name = "receipt_no", nullable = false, length = 20)
    private String receiptNo;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payment_student"))
    private Student student;

    @NotNull(message = "Ledger is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "ledger_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payment_ledger"))
    private StudentFeeLedger ledger;

    @NotNull(message = "Invoice is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "invoice_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_payment_invoice"))
    private Invoice invoice;

    @NotNull(message = "Payment date is required")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotNull(message = "Amount paid is required")
    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @NotNull(message = "Payment mode is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false, length = 20)
    private PaymentMode paymentMode;

    @Size(max = 50, message = "Transaction reference must not exceed 50 characters")
    @Column(name = "transaction_ref", length = 50)
    private String transactionRef;

    @Size(max = 20, message = "Cheque number must not exceed 20 characters")
    @Column(name = "cheque_no", length = 20)
    private String chequeNo;

    @Column(name = "cheque_date")
    private LocalDate chequeDate;

    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    @Column(name = "bank_name", length = 100)
    private String bankName;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentRecordStatus status = PaymentRecordStatus.SUCCESS;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(
            name = "collected_by",
            foreignKey = @ForeignKey(name = "fk_payment_collected_by"))
    private User collectedByUser;
}
