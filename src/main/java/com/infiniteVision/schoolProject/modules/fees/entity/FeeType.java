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
 * Master fee type (tuition, transport, exam, etc.). Maps to {@code fee_types}.
 */
@Entity
@Table(
        name = "fee_types",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_fee_types_code", columnNames = "fee_type_code")
        },
        indexes = {
                @Index(name = "idx_fee_types_active", columnList = "is_active")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "fee_type_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class FeeType extends BaseEntity {

    @NotBlank(message = "Fee type code is required")
    @Size(max = 30, message = "Fee type code must not exceed 30 characters")
    @Column(name = "fee_type_code", nullable = false, length = 30)
    private String feeTypeCode;

    @NotBlank(message = "Fee type name is required")
    @Size(max = 100, message = "Fee type name must not exceed 100 characters")
    @Column(name = "fee_type_name", nullable = false, length = 100)
    private String feeTypeName;

    @NotNull(message = "Active flag is required")
    @Column(name = "is_active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @NotNull(message = "Display order is required")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
}
