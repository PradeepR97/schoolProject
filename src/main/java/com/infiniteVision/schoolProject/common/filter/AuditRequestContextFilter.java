package com.infiniteVision.schoolProject.common.filter;

import com.infiniteVision.schoolProject.common.audit.context.AuditRequestContext;
import com.infiniteVision.schoolProject.common.audit.context.AuditRequestContextHolder;
import com.infiniteVision.schoolProject.constants.ApiConstants;
import com.infiniteVision.schoolProject.constants.AuditConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Captures request id, client IP, and API path for {@code audit_logs} rows on {@code /api/v1/**} requests.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AuditRequestContextFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith(ApiConstants.API_V1_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = request.getHeader(AuditConstants.REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        String apiPath = request.getMethod() + " " + request.getRequestURI();
        AuditRequestContextHolder.set(new AuditRequestContext(requestId.trim(), request.getRemoteAddr(), apiPath));

        try {
            filterChain.doFilter(request, response);
        } finally {
            AuditRequestContextHolder.clear();
        }
    }
}
