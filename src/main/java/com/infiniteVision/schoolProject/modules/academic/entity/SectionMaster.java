package com.infiniteVision.schoolProject.modules.academic.entity;

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
 * Reusable section labels (A, B, C, etc.). Maps to {@code section_master}.
 */
@Entity
@Table(
        name = "section_master",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_section_code", columnNames = "section_code")
        },
        indexes = {
                @Index(name = "idx_section_active", columnList = "is_active")
        })
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "section_id", updatable = false, nullable = false)),
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false, nullable = false))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SectionMaster extends BaseEntity {

    @NotBlank(message = "Section code is required")
    @Size(max = 5, message = "Section code must not exceed 5 characters")
    @Column(name = "section_code", nullable = false, length = 5)
    private String sectionCode;

    @Size(max = 50, message = "Section name must not exceed 50 characters")
    @Column(name = "section_name", length = 50)
    private String sectionName;

    @NotNull(message = "Display order is required")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @NotNull(message = "Active flag is required")
    @Column(name = "is_active", nullable = false)
    private Boolean active = Boolean.TRUE;
}
