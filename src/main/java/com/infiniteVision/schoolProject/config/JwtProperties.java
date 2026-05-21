package com.infiniteVision.schoolProject.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bearer token and Redis session settings (opaque token, not signed JWT).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** Opaque Bearer token length in characters (hex-encoded random bytes). */
    private int tokenLength = 128;

    private long expirationMs;
    private String redisPrefix = "session:";
}
