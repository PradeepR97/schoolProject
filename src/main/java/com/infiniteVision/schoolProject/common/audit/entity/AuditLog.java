package com.infiniteVision.schoolProject.common.audit.entity;

import com.infiniteVision.schoolProject.common.audit.enums.AuditAction;
import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Immutable change-history row for cross-module audit queries.
 * <p>
 * Does not extend {@link com.infiniteVision.schoolProject.common.entity.BaseEntity} — append-only, never soft-deleted.
 */
@Entity
@Table(
        name = "audit_logs",
        indexes = {
            @Index(name = "idx_audit_entity", columnList = "entity_type, entity_id"),
            @Index(name = "idx_audit_session", columnList = "session_id"),
            @Index(name = "idx_audit_performed_at", columnList = "performed_at"),
            @Index(name = "idx_audit_performed_by", columnList = "performed_by")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id", updatable = false, nullable = false)
    private Long auditId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 100)
    private AuditEntityType entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private AuditAction action;

    @Column(name = "changed_fields", columnDefinition = "JSON")
    private String changedFields;

    @Column(name = "snapshot_before", columnDefinition = "JSON")
    private String snapshotBefore;

    @Column(name = "snapshot_after", columnDefinition = "JSON")
    private String snapshotAfter;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "session_id", length = 36)
    private UUID sessionId;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    @Column(name = "request_id", length = 64)
    private String requestId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "api_path", length = 255)
    private String apiPath;

    @Column(name = "remarks", length = 500)
    private String remarks;
}
