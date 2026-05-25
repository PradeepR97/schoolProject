package com.infiniteVision.schoolProject.security;

import com.infiniteVision.schoolProject.constants.ApiConstants;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.health.constants.HealthApiConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Validates {@code Authorization: Bearer} opaque token on protected routes and sets Spring Security context.
 * <p>
 * Skipped for public paths: health check and login. Token must exist in Redis — logout deletes the key.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String LOGIN_PATH = ApiConstants.API_V1_PREFIX + "/auth/login";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /** Public endpoints that do not require a Bearer token. */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return HealthApiConstants.BASE_PATH.equals(path) || LOGIN_PATH.equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length()).trim();
            if (!token.isEmpty()) {
                try {
                    JwtService.TokenClaims claims = jwtService.resolveToken(token);
                    User user = userRepository.findById(claims.userId()).orElse(null);
                    if (user != null && !Boolean.TRUE.equals(user.getDeleted())) {
                        AuthenticatedUser principal = new AuthenticatedUser(
                                user.getId(),
                                user.getUsername(),
                                user.getRole(),
                                claims.sessionId(),
                                claims.token());
                        var authentication = new UsernamePasswordAuthenticationToken(
                                principal, null, principal.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (UnauthorizedException ignored) {
                    SecurityContextHolder.clearContext();
                }
            }
        }
        chain.doFilter(request, response);
    }
}
