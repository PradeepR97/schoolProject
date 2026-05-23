package com.infiniteVision.schoolProject.common.audit.util;

import tools.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Builds field-level old/new maps for audit {@code changed_fields} JSON.
 */
@Component
@RequiredArgsConstructor
public class AuditDiffUtil {

    private final ObjectMapper objectMapper;

    /**
     * Compares two snapshots (typically DTOs) and returns only keys whose values differ.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> buildChangedFields(Object beforeSnapshot, Object afterSnapshot) {
        if (beforeSnapshot == null || afterSnapshot == null) {
            return Map.of();
        }
        Map<String, Object> beforeMap = objectMapper.convertValue(beforeSnapshot, Map.class);
        Map<String, Object> afterMap = objectMapper.convertValue(afterSnapshot, Map.class);

        Map<String, Map<String, Object>> changes = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : afterMap.entrySet()) {
            String key = entry.getKey();
            Object oldValue = beforeMap.get(key);
            Object newValue = entry.getValue();
            if (!Objects.equals(oldValue, newValue)) {
                Map<String, Object> change = new LinkedHashMap<>();
                change.put("old", oldValue);
                change.put("new", newValue);
                changes.put(key, change);
            }
        }
        for (Map.Entry<String, Object> entry : beforeMap.entrySet()) {
            if (!afterMap.containsKey(entry.getKey())) {
                Map<String, Object> change = new LinkedHashMap<>();
                change.put("old", entry.getValue());
                change.put("new", null);
                changes.put(entry.getKey(), change);
            }
        }
        return changes;
    }
}
