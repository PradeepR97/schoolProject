package com.infiniteVision.schoolProject.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cross-origin settings for browser clients (SPA, admin portal, etc.).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * Explicit origin URLs only — do not use {@code *} when {@link #allowCredentials} is true.
     */
    private List<String> allowedOrigins = List.of(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173");

    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

    private List<String> allowedHeaders = List.of("Authorization", "Content-Type", "Accept", "X-Requested-With");

    private List<String> exposedHeaders = List.of();

    private boolean allowCredentials = true;

    /** Preflight cache duration in seconds. */
    private long maxAge = 3600L;
}
