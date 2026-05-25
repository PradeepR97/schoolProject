package com.infiniteVision.schoolProject.common.audit.context;

import java.util.UUID;

/**
 * Who performed an action: user id and optional login session UUID.
 */
public record AuditParticipant(String performedBy, UUID sessionId) {

    public static AuditParticipant of(Long userId, UUID sessionId) {
        if (userId == null) {
            return new AuditParticipant(null, sessionId);
        }
        return new AuditParticipant(String.valueOf(userId), sessionId);
    }
}
