package com.infiniteVision.schoolProject.common.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Builds HTTP headers for binary file downloads (.xlsx, .pdf, etc.).
 */
public final class FileAttachmentHeaders {

    private FileAttachmentHeaders() {}

    /**
     * Response with Content-Type, Content-Length, and Content-Disposition (ASCII + RFC 5987 filename*).
     */
    public static ResponseEntity<byte[]> okAttachment(byte[] body, MediaType mediaType, String filename) {
        HttpHeaders headers = attachmentHeaders(body, mediaType, filename);
        return ResponseEntity.ok().headers(headers).body(body);
    }

    public static HttpHeaders attachmentHeaders(byte[] body, MediaType mediaType, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        if (body != null) {
            headers.setContentLength(body.length);
        }
        headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(filename));
        return headers;
    }

    /**
     * attachment; filename="fallback.xlsx"; filename*=UTF-8''student-report.xlsx
     */
    public static String contentDisposition(String filename) {
        String safeFilename = filename != null ? filename : "download";
        String asciiFallback = safeFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (asciiFallback.isBlank()) {
            asciiFallback = "download";
        }
        String encoded = URLEncoder.encode(safeFilename, StandardCharsets.UTF_8).replace("+", "%20");
        return "attachment; filename=\"" + asciiFallback + "\"; filename*=UTF-8''" + encoded;
    }
}
