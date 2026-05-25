package com.infiniteVision.schoolProject.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Reusable audit and soft-delete fields for all domain entities (users, students, sessions, etc.).
 * <p>
 * {@link CreatedBy} / {@link LastModifiedBy} store the authenticated user's numeric ID as a string,
 * populated via {@code SecurityAuditorAware} when {@code @EnableJpaAuditing} is active.
 * {@link #deletedBy} is set explicitly on soft delete via {@link #markDeleted(Long)}.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @CreatedDate
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Authenticated user ID who created the row (string form of Long). */
    @CreatedBy
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @LastModifiedDate
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Authenticated user ID who last updated the row (string form of Long). */
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /** Soft-delete flag; defaults to {@code false} for active rows. */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = Boolean.FALSE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** Authenticated user ID who soft-deleted the row (string form of Long). */
    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    /**
     * Soft-deletes this entity: sets {@code deleted}, {@code deletedAt}, and {@code deletedBy}.
     *
     * @param deletedByUserId ID of the authenticated user performing the delete
     */
    public void markDeleted(Long deletedByUserId) {
        this.deleted = Boolean.TRUE;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByUserId != null ? String.valueOf(deletedByUserId) : null;
    }
}
