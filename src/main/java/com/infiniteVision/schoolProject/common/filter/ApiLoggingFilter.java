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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 * Logs request and response bodies for {@code /api/**} REST endpoints.
 * Binary file downloads (Excel/PDF) log a short confirmation instead of file bytes.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class ApiLoggingFilter extends OncePerRequestFilter {

    private static final Pattern CONTENT_DISPOSITION_FILENAME =
            Pattern.compile("filename\\*?=(?:UTF-8''|\"?)([^\";]+)");

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
        String requestBody = isFileDownloadPath(request.getRequestURI())
                ? ""
                : formatBody(extractRequestBody(request));
        log.info(
                "{} | api={} | method={} | uri={} | query={} | clientIp={} | authorization={} | requestBody={}",
                LoggingConstants.API_REQUEST_LOG_PREFIX,
                apiName,
                request.getMethod(),
                request.getRequestURI(),
                nullToEmpty(request.getQueryString()),
                request.getRemoteAddr(),
                ApiLogBodyMasker.maskAuthorizationHeader(request.getHeader(HttpHeaders.AUTHORIZATION)),
                requestBody);
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
                formatResponseBodyForLog(request, response));
    }

    private String formatResponseBodyForLog(
            HttpServletRequest request, ContentCachingResponseWrapper response) {
        if (!isBinaryFileDownload(request, response)) {
            return formatBody(extractResponseBody(response));
        }
        int sizeBytes = response.getContentAsByteArray().length;
        String filename = extractFilenameFromContentDisposition(response.getHeader(HttpHeaders.CONTENT_DISPOSITION));
        StringBuilder summary = new StringBuilder(LoggingConstants.FILE_DOWNLOADED_MESSAGE);
        if (!filename.isBlank()) {
            summary.append(" | filename=").append(filename);
        }
        summary.append(" | sizeBytes=").append(sizeBytes);
        return summary.toString();
    }

    private static boolean isBinaryFileDownload(
            HttpServletRequest request, ContentCachingResponseWrapper response) {
        if (isFileDownloadPath(request.getRequestURI())) {
            return true;
        }
        String contentType = response.getContentType();
        if (contentType == null) {
            return false;
        }
        String lower = contentType.toLowerCase();
        return lower.contains("spreadsheetml")
                || lower.contains("application/pdf")
                || lower.contains("application/octet-stream");
    }

    private static boolean isFileDownloadPath(String uri) {
        if (uri == null) {
            return false;
        }
        if (uri.contains("/reports/") && uri.endsWith("/export")) {
            return true;
        }
        return uri.contains("/excel-upload/")
                && (uri.endsWith("/template") || uri.endsWith("/sample"));
    }

    private static String extractFilenameFromContentDisposition(String contentDisposition) {
        if (contentDisposition == null || contentDisposition.isBlank()) {
            return "";
        }
        Matcher matcher = CONTENT_DISPOSITION_FILENAME.matcher(contentDisposition);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
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
