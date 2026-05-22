package com.infiniteVision.schoolProject.util;

import com.infiniteVision.schoolProject.constants.LoggingConstants;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Masks sensitive fields in JSON bodies and Authorization headers before API logging.
 */
public final class ApiLogBodyMasker {

    private static final Set<String> SENSITIVE_JSON_FIELDS = Set.of(
            "password",
            "currentpassword",
            "newpassword",
            "confirmnewpassword",
            "token",
            "sessiontoken",
            "accesstoken",
            "refreshtoken",
            "otp",
            "authorization");

    private static final Pattern JSON_FIELD_PATTERN = Pattern.compile(
            "\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"",
            Pattern.CASE_INSENSITIVE);

    private ApiLogBodyMasker() {
    }

    public static String maskJsonBody(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }
        return JSON_FIELD_PATTERN.matcher(body).replaceAll(matchResult -> {
            String fieldName = matchResult.group(1);
            if (isSensitiveField(fieldName)) {
                return "\"" + fieldName + "\":\"" + LoggingConstants.MASKED_VALUE + "\"";
            }
            return matchResult.group(0);
        });
    }

    public static String maskAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return authorizationHeader;
        }
        if (authorizationHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return "Bearer " + LoggingConstants.MASKED_VALUE;
        }
        return LoggingConstants.MASKED_VALUE;
    }

    private static boolean isSensitiveField(String fieldName) {
        return SENSITIVE_JSON_FIELDS.contains(fieldName.toLowerCase());
    }
}
