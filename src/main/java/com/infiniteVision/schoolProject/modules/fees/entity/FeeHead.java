package com.infiniteVision.schoolProject.modules.fees.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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

/**
 * Master fee category (tuition, transport, etc.). Maps to {@code fee_heads}.
 */
@Entity
@Table(
        name = "fee_heads",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_fee_heads_code", columnNames = "fee_head_code")
        },
        indexes = {
                @Index(name = "idx_fee_heads_category", columnList = "fee_category"),
                @Index(name = "idx_fee_heads_active", columnList = "is_active")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "fee_head_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class FeeHead extends BaseEntity {

    @NotBlank(message = "Fee head code is required")
    @Size(max = 30, message = "Fee head code must not exceed 30 characters")
    @Column(name = "fee_head_code", nullable = false, length = 30)
    private String feeHeadCode;

    @NotBlank(message = "Fee head name is required")
    @Size(max = 100, message = "Fee head name must not exceed 100 characters")
    @Column(name = "fee_head_name", nullable = false, length = 100)
    private String feeHeadName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Grouping label, e.g. ACADEMIC, TRANSPORT, HOSTEL, OPTIONAL. */
    @Size(max = 50, message = "Fee category must not exceed 50 characters")
    @Column(name = "fee_category", length = 50)
    private String feeCategory;

    @NotNull(message = "Mandatory flag is required")
    @Column(name = "is_mandatory", nullable = false)
    private Boolean mandatory = Boolean.TRUE;

    @NotNull(message = "Refundable flag is required")
    @Column(name = "is_refundable", nullable = false)
    private Boolean refundable = Boolean.FALSE;

    @NotNull(message = "Active flag is required")
    @Column(name = "is_active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @NotNull(message = "Display order is required")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
}
