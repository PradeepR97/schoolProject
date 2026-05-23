package com.infiniteVision.schoolProject.common.audit.service.impl;

import com.infiniteVision.schoolProject.common.audit.context.AuditContextResolver;
import com.infiniteVision.schoolProject.common.audit.context.AuditParticipant;
import com.infiniteVision.schoolProject.common.audit.context.AuditRequestContext;
import com.infiniteVision.schoolProject.common.audit.entity.AuditLog;
import com.infiniteVision.schoolProject.common.audit.enums.AuditAction;
import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.repository.AuditLogRepository;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.common.audit.util.AuditDiffUtil;
import com.infiniteVision.schoolProject.common.audit.util.AuditJsonWriter;
import com.infiniteVision.schoolProject.constants.AuditConstants;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Writes append-only rows to {@code audit_logs} with actor, session, request metadata, and masked JSON snapshots.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditJsonWriter auditJsonWriter;
    private final AuditDiffUtil auditDiffUtil;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void logCreate(AuditEntityType entityType, Long entityId, Object afterSnapshot) {
        persist(
                entityType,
                entityId,
                AuditAction.CREATE,
                null,
                afterSnapshot,
                resolveParticipantOrEmpty(),
                null);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void logCreate(
            AuditEntityType entityType,
            Long entityId,
            Object afterSnapshot,
            AuditParticipant participant,
            AuditAction action,
            String remarks) {
        persist(entityType, entityId, action, null, afterSnapshot, participant, remarks);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void logUpdate(AuditEntityType entityType, Long entityId, Object beforeSnapshot, Object afterSnapshot) {
        persist(
                entityType,
                entityId,
                AuditAction.UPDATE,
                beforeSnapshot,
                afterSnapshot,
                resolveParticipantOrEmpty(),
                null);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void logDelete(AuditEntityType entityType, Long entityId, Object beforeSnapshot) {
        persist(
                entityType,
                entityId,
                AuditAction.DELETE,
                beforeSnapshot,
                null,
                resolveParticipantOrEmpty(),
                null);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void logAuthEvent(
            AuditEntityType entityType,
            Long entityId,
            AuditAction action,
            Object snapshot,
            AuditParticipant participant,
            String remarks) {
        Object before = action == AuditAction.LOGOUT ? snapshot : null;
        Object after = action == AuditAction.LOGIN ? snapshot : null;
        persist(entityType, entityId, action, before, after, participant, remarks);
    }

    private void persist(
            AuditEntityType entityType,
            Long entityId,
            AuditAction action,
            Object beforeSnapshot,
            Object afterSnapshot,
            AuditParticipant participant,
            String remarks) {
        try {
            AuditRequestContext requestContext = AuditContextResolver.resolveRequestContext();
            String changedFieldsJson = null;
            if (action == AuditAction.UPDATE && beforeSnapshot != null && afterSnapshot != null) {
                Map<String, Map<String, Object>> changes =
                        auditDiffUtil.buildChangedFields(beforeSnapshot, afterSnapshot);
                if (!changes.isEmpty()) {
                    changedFieldsJson = objectMapper.writeValueAsString(changes);
                }
            }

            AuditLog auditLog = AuditLog.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .action(action)
                    .changedFields(changedFieldsJson)
                    .snapshotBefore(auditJsonWriter.toMaskedJson(beforeSnapshot))
                    .snapshotAfter(auditJsonWriter.toMaskedJson(afterSnapshot))
                    .performedBy(truncate(participant.performedBy(), AuditConstants.PERFORMED_BY_MAX_LENGTH))
                    .sessionId(participant.sessionId())
                    .performedAt(LocalDateTime.now())
                    .requestId(truncate(requestContext.requestId(), AuditConstants.REQUEST_ID_MAX_LENGTH))
                    .ipAddress(truncate(requestContext.ipAddress(), AuditConstants.IP_ADDRESS_MAX_LENGTH))
                    .apiPath(truncate(requestContext.apiPath(), AuditConstants.API_PATH_MAX_LENGTH))
                    .remarks(truncate(remarks, AuditConstants.REMARKS_MAX_LENGTH))
                    .build();

            auditLogRepository.save(auditLog);
        } catch (JacksonException exception) {
            log.error(
                    "Failed to persist audit log entityType={} entityId={} action={}",
                    entityType,
                    entityId,
                    action,
                    exception);
        } catch (RuntimeException exception) {
            log.error(
                    "Unexpected error persisting audit log entityType={} entityId={} action={}",
                    entityType,
                    entityId,
                    action,
                    exception);
        }
    }

    private AuditParticipant resolveParticipantOrEmpty() {
        Optional<AuditParticipant> participant = AuditContextResolver.resolveParticipant();
        return participant.orElse(new AuditParticipant(null, null));
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }
}
