package com.infiniteVision.schoolProject.modules.academic.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * School grade (e.g. 10, LKG) for an academic year. Maps to {@code class_master}.
 * <p>
 * Section is stored separately on students, fee structures, and invoices via {@link SectionMaster}.
 */
@Entity
@Table(
        name = "class_master",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_class_name_year",
                        columnNames = {"class_name", "academic_year_id"})
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "class_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"academicYear"})
public class ClassMaster extends BaseEntity {

    @NotBlank(message = "Class name is required")
    @Size(max = 20, message = "Class name must not exceed 20 characters")
    @Column(name = "class_name", nullable = false, length = 20)
    private String className;

    @Size(max = 100, message = "Class teacher name must not exceed 100 characters")
    @Column(name = "class_teacher", length = 100)
    private String classTeacher;

    @NotNull(message = "Academic year is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "academic_year_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_class_academic_year"))
    private AcademicYear academicYear;
}
