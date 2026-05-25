package com.infiniteVision.schoolProject.modules.academic.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * School academic year (e.g. 2025-26). Maps to {@code academic_year}.
 */
@Entity
@Table(
        name = "academic_year",
        uniqueConstraints = {@UniqueConstraint(name = "uq_year_name", columnNames = "year_name")})
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "year_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AcademicYear extends BaseEntity {

    @NotBlank(message = "Year name is required")
    @Size(max = 20, message = "Year name must not exceed 20 characters")
    @Column(name = "year_name", nullable = false, length = 20)
    private String yearName;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Current year flag is required")
    @Column(name = "is_current", nullable = false)
    private Boolean current = Boolean.FALSE;
}
