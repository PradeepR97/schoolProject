package com.infiniteVision.schoolProject.security;

import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security principal for Bearer-authenticated users.
 * <p>
 * Set by {@link JwtAuthenticationFilter}. {@code sessionToken} is the opaque Bearer token;
 * {@code sessionId} links to {@code user_sessions.session_id} for logout.
 */
@Getter
public class AuthenticatedUser implements UserDetails {

    private final Long userId;
    private final String username;
    private final UserRole role;
    private final UUID sessionId;
    /** Opaque Bearer token — Redis key {@code session:token:{sessionToken}}. */
    private final String sessionToken;

    public AuthenticatedUser(
            Long userId, String username, UserRole role, UUID sessionId, String sessionToken) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.sessionId = sessionId;
        this.sessionToken = sessionToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
