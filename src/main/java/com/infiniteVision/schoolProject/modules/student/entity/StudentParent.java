package com.infiniteVision.schoolProject.modules.student.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.student.enums.PrimaryContact;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Father, mother, and guardian contact details for a single student.
 * Maps to {@code student_parents}. One row per student ({@code student_id} unique).
 */
@Entity
@Table(
        name = "student_parents",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_student_parents_student", columnNames = "student_id")
        })
@AttributeOverrides({
        @AttributeOverride(
                name = "id",
                column = @Column(name = "student_parent_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = "student")
public class StudentParent extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Size(max = 150, message = "Father name must not exceed 150 characters")
    @Column(name = "father_name", length = 150)
    private String fatherName;

    @Size(max = 15, message = "Father phone must not exceed 15 characters")
    @Column(name = "father_phone", length = 15)
    private String fatherPhone;

    @Email(message = "Father email must be a valid address")
    @Size(max = 100, message = "Father email must not exceed 100 characters")
    @Column(name = "father_email", length = 100)
    private String fatherEmail;

    @Size(max = 100, message = "Father occupation must not exceed 100 characters")
    @Column(name = "father_occupation", length = 100)
    private String fatherOccupation;

    @Column(name = "father_annual_income", precision = 10, scale = 2)
    private BigDecimal fatherAnnualIncome;

    @Size(max = 150, message = "Father qualification must not exceed 150 characters")
    @Column(name = "father_qualification", length = 150)
    private String fatherQualification;

    @Size(max = 150, message = "Mother name must not exceed 150 characters")
    @Column(name = "mother_name", length = 150)
    private String motherName;

    @Size(max = 15, message = "Mother phone must not exceed 15 characters")
    @Column(name = "mother_phone", length = 15)
    private String motherPhone;

    @Email(message = "Mother email must be a valid address")
    @Size(max = 100, message = "Mother email must not exceed 100 characters")
    @Column(name = "mother_email", length = 100)
    private String motherEmail;

    @Size(max = 100, message = "Mother occupation must not exceed 100 characters")
    @Column(name = "mother_occupation", length = 100)
    private String motherOccupation;

    @Column(name = "mother_annual_income", precision = 10, scale = 2)
    private BigDecimal motherAnnualIncome;

    @Size(max = 150, message = "Mother qualification must not exceed 150 characters")
    @Column(name = "mother_qualification", length = 150)
    private String motherQualification;

    @Size(max = 150, message = "Guardian name must not exceed 150 characters")
    @Column(name = "guardian_name", length = 150)
    private String guardianName;

    @Size(max = 15, message = "Guardian phone must not exceed 15 characters")
    @Column(name = "guardian_phone", length = 15)
    private String guardianPhone;

    @Email(message = "Guardian email must be a valid address")
    @Size(max = 100, message = "Guardian email must not exceed 100 characters")
    @Column(name = "guardian_email", length = 100)
    private String guardianEmail;

    @Size(max = 100, message = "Guardian occupation must not exceed 100 characters")
    @Column(name = "guardian_occupation", length = 100)
    private String guardianOccupation;

    @Size(max = 150, message = "Guardian qualification must not exceed 150 characters")
    @Column(name = "guardian_qualification", length = 150)
    private String guardianQualification;

    @Size(max = 50, message = "Guardian relationship must not exceed 50 characters")
    @Column(name = "guardian_relationship", length = 50)
    private String guardianRelationship;

    @NotNull(message = "Primary contact is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "primary_contact", nullable = false, length = 20)
    private PrimaryContact primaryContact = PrimaryContact.FATHER;
}
