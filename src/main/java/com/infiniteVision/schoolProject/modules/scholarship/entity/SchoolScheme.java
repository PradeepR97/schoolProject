package com.infiniteVision.schoolProject.modules.scholarship.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ApplicableTo;
import com.infiniteVision.schoolProject.modules.scholarship.enums.DiscountType;
import com.infiniteVision.schoolProject.modules.scholarship.enums.SchemeType;
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
import jakarta.validation.constraints.DecimalMax;
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
 * Scholarship / fee discount scheme for an academic year (scholarship module).
 * Maps to {@code school_schemes}.
 * <p>
 * {@code createdAt} and other audit fields are inherited from {@link BaseEntity}.
 */
@Entity
@Table(
        name = "school_schemes",
        indexes = {
                @Index(name = "idx_school_schemes_academic_year", columnList = "academic_year_id"),
                @Index(name = "idx_school_schemes_fee_head", columnList = "fee_head_id"),
                @Index(name = "idx_school_schemes_type", columnList = "scheme_type"),
                @Index(name = "idx_school_schemes_active", columnList = "is_active")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "scheme_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"academicYear", "feeHead"})
public class SchoolScheme extends BaseEntity {

    @NotBlank(message = "Scheme name is required")
    @Size(max = 150, message = "Scheme name must not exceed 150 characters")
    @Column(name = "scheme_name", nullable = false, length = 150)
    private String schemeName;

    @NotNull(message = "Scheme type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "scheme_type", nullable = false, length = 30)
    private SchemeType schemeType;

    @NotNull(message = "Discount type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @PositiveOrZero(message = "Discount value must be zero or positive")
    @DecimalMax(value = "9999999999.99", message = "Discount value is too large")
    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @NotNull(message = "Applicable to is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "applicable_to", nullable = false, length = 30)
    private ApplicableTo applicableTo;

    /**
     * Required when {@link #applicableTo} is {@link ApplicableTo#SPECIFIC_HEAD}; otherwise optional.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "fee_head_id",
            foreignKey = @ForeignKey(name = "fk_school_schemes_fee_head"))
    private FeeHead feeHead;

    @NotNull(message = "Academic year is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(
            name = "academic_year_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_school_schemes_academic_year"))
    private AcademicYear academicYear;

    @NotNull(message = "Active flag is required")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;
}
