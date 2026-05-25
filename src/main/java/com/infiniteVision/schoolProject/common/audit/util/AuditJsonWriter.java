package com.infiniteVision.schoolProject.common.audit.util;

import com.infiniteVision.schoolProject.util.ApiLogBodyMasker;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Serializes audit snapshots to JSON with sensitive fields masked.
 */
@Component
@RequiredArgsConstructor
public class AuditJsonWriter {

    private final ObjectMapper objectMapper;

    /**
     * Converts a snapshot object to masked JSON, or {@code null} when the source is {@code null}.
     */
    public String toMaskedJson(Object snapshot) {
        if (snapshot == null) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(snapshot);
            return ApiLogBodyMasker.maskJsonBody(json);
        } catch (JacksonException exception) {
            return "{\"serializationError\":\"" + escape(exception.getMessage()) + "\"}";
        }
    }

    private String escape(String message) {
        if (message == null) {
            return "";
        }
        return message.replace("\"", "'");
    }
}
