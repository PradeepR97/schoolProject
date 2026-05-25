package com.infiniteVision.schoolProject.common.audit.context;

import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolves audit actor and HTTP context from Spring Security and the request filter.
 */
public final class AuditContextResolver {

    private AuditContextResolver() {
    }

    public static Optional<AuditParticipant> resolveParticipant() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return Optional.of(AuditParticipant.of(principal.getUserId(), principal.getSessionId()));
        }
        return Optional.empty();
    }

    public static AuditRequestContext resolveRequestContext() {
        return AuditRequestContextHolder.get();
    }
}
