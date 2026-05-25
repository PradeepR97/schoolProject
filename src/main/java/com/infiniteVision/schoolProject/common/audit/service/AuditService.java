package com.infiniteVision.schoolProject.common.audit.service;

import com.infiniteVision.schoolProject.common.audit.context.AuditParticipant;
import com.infiniteVision.schoolProject.common.audit.enums.AuditAction;
import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;

/**
 * Persists immutable change-history rows to {@code audit_logs}.
 */
public interface AuditService {

    /**
     * Records a new entity (or aggregate) creation.
     *
     * @param entityType domain category
     * @param entityId primary key of the affected row
     * @param afterSnapshot DTO or map representing state after the change (passwords masked in JSON)
     */
    void logCreate(AuditEntityType entityType, Long entityId, Object afterSnapshot);

    /**
     * Records creation with an explicit actor (e.g. login before SecurityContext is set).
     */
    void logCreate(
            AuditEntityType entityType,
            Long entityId,
            Object afterSnapshot,
            AuditParticipant participant,
            AuditAction action,
            String remarks);

    /**
     * Records an update with before/after snapshots and computed field diffs.
     */
    void logUpdate(AuditEntityType entityType, Long entityId, Object beforeSnapshot, Object afterSnapshot);

    /**
     * Records an update with optional remarks (e.g. password change where the DTO diff is empty).
     */
    void logUpdate(
            AuditEntityType entityType,
            Long entityId,
            Object beforeSnapshot,
            Object afterSnapshot,
            String remarks);

    /**
     * Records a soft delete or removal; stores the last known state in {@code snapshot_before}.
     */
    void logDelete(AuditEntityType entityType, Long entityId, Object beforeSnapshot);

    /**
     * Records a session or auth event (login/logout) with explicit actor and session id.
     */
    void logAuthEvent(
            AuditEntityType entityType,
            Long entityId,
            AuditAction action,
            Object snapshot,
            AuditParticipant participant,
            String remarks);
}
