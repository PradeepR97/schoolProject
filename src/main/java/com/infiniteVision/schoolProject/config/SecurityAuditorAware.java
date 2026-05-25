package com.infiniteVision.schoolProject.config;

import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Supplies the current authenticated user's ID for JPA {@code @CreatedBy} and {@code @LastModifiedBy}.
 */
@Component("securityAuditorAware")
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return Optional.of(String.valueOf(principal.getUserId()));
        }
        return Optional.empty();
    }
}
