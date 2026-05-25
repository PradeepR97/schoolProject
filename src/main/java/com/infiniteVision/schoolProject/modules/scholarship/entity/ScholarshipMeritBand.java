package com.infiniteVision.schoolProject.modules.scholarship.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
 * Mark-based tuition waiver band for an academic year (e.g. 490+ → 100%).
 */
@Entity
@Table(
        name = "scholarship_merit_bands",
        indexes = {@Index(name = "idx_merit_band_year", columnList = "academic_year_id")})
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "band_id", updatable = false, nullable = false)),
    @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"academicYear"})
public class ScholarshipMeritBand extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "academic_year_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_merit_band_year"))
    private AcademicYear academicYear;

    @NotNull
    @Column(name = "min_mark", nullable = false, precision = 6, scale = 2)
    private BigDecimal minMark;

    @Column(name = "max_mark", precision = 6, scale = 2)
    private BigDecimal maxMark;

    @NotNull
    @PositiveOrZero
    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @NotBlank
    @Size(max = 100)
    @Column(name = "band_label", nullable = false, length = 100)
    private String bandLabel;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean active = Boolean.TRUE;
}
