package com.infiniteVision.schoolProject.common.filter;

import com.infiniteVision.schoolProject.constants.ApiConstants;
import com.infiniteVision.schoolProject.constants.LoggingConstants;
import com.infiniteVision.schoolProject.util.ApiLogBodyMasker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Logs request and response bodies for all {@code /api/**} REST endpoints.
 * <p>
 * Sensitive values (password, token, Bearer header) are masked before logging.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class ApiLoggingFilter extends OncePerRequestFilter {

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

        ContentCachingRequestWrapper cachedRequest =
                new ContentCachingRequestWrapper(request, LoggingConstants.MAX_LOG_BODY_CHARACTERS);
        ContentCachingResponseWrapper cachedResponse = new ContentCachingResponseWrapper(response);

        String apiName = buildApiName(cachedRequest);
        long startedAt = System.currentTimeMillis();

        logRequest(apiName, cachedRequest);

        try {
            filterChain.doFilter(cachedRequest, cachedResponse);
        } finally {
            logResponse(apiName, cachedRequest, cachedResponse, startedAt);
            cachedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(String apiName, ContentCachingRequestWrapper request) {
        if (!log.isInfoEnabled()) {
            return;
        }
        log.info(
                "{} | api={} | method={} | uri={} | query={} | clientIp={} | authorization={} | requestBody={}",
                LoggingConstants.API_REQUEST_LOG_PREFIX,
                apiName,
                request.getMethod(),
                request.getRequestURI(),
                nullToEmpty(request.getQueryString()),
                request.getRemoteAddr(),
                ApiLogBodyMasker.maskAuthorizationHeader(request.getHeader(HttpHeaders.AUTHORIZATION)),
                formatBody(extractRequestBody(request)));
    }

    private void logResponse(
            String apiName,
            HttpServletRequest request,
            ContentCachingResponseWrapper response,
            long startedAt) {
        if (!log.isInfoEnabled()) {
            return;
        }
        long durationMs = System.currentTimeMillis() - startedAt;
        log.info(
                "{} | api={} | method={} | uri={} | status={} | durationMs={} | responseBody={}",
                LoggingConstants.API_RESPONSE_LOG_PREFIX,
                apiName,
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                durationMs,
                formatBody(extractResponseBody(response)));
    }

    private String buildApiName(HttpServletRequest request) {
        return request.getMethod() + " " + request.getRequestURI();
    }

    private String extractRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        return decode(content, request.getCharacterEncoding());
    }

    private String extractResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        return decode(content, response.getCharacterEncoding());
    }

    private String decode(byte[] content, String encoding) {
        Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
        return new String(content, charset);
    }

    private String formatBody(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }
        String masked = ApiLogBodyMasker.maskJsonBody(body);
        if (masked.length() <= LoggingConstants.MAX_LOG_BODY_CHARACTERS) {
            return masked;
        }
        return masked.substring(0, LoggingConstants.MAX_LOG_BODY_CHARACTERS) + "...[truncated]";
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
