package com.infiniteVision.schoolProject.security;

import com.infiniteVision.schoolProject.config.JwtProperties;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Opaque Bearer token generation (fixed length) and Redis session allow-list.
 * <p>
 * Redis value format: {@code userId|role|sessionId} under {@code {prefix}token:{token}}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final int HEX_CHARS_PER_BYTE = 2;
    private static final String SESSION_VALUE_SEPARATOR = "|";
    private static final int SESSION_VALUE_PART_COUNT = 3;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;

    /** Resolved session from Redis after Bearer token lookup. */
    public record TokenClaims(Long userId, String token, UserRole role, UUID sessionId) {
    }

    /**
     * Creates a cryptographically random opaque token (hex, length {@code jwt.token-length}).
     */
    public String generateSessionToken() {
        int tokenLength = jwtProperties.getTokenLength();
        if (tokenLength % HEX_CHARS_PER_BYTE != 0) {
            throw new IllegalStateException("jwt.token-length must be even for hex encoding");
        }
        byte[] bytes = new byte[tokenLength / HEX_CHARS_PER_BYTE];
        SECURE_RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    /**
     * Loads user id, role, and session id from Redis; rejects unknown, expired, or malformed tokens.
     */
    public TokenClaims resolveToken(String token) {
        validateTokenFormat(token);
        String value = stringRedisTemplate.opsForValue().get(tokenKey(token));
        if (value == null || value.isBlank()) {
            throw new UnauthorizedException("Invalid token");
        }
        return parseSessionValue(token, value);
    }

    /** Token lifetime in seconds (for API {@code expiresIn} field). */
    public long expirationSeconds() {
        return jwtProperties.getExpirationMs() / 1000L;
    }

    /**
     * Registers an active session; TTL matches {@code jwt.expiration-ms}.
     */
    public void storeSession(Long userId, String token, UserRole role, UUID sessionId) {
        Duration ttl = Duration.ofMillis(jwtProperties.getExpirationMs());
        String sessionValue = userId + SESSION_VALUE_SEPARATOR + role.name() + SESSION_VALUE_SEPARATOR + sessionId;
        stringRedisTemplate.opsForValue().set(tokenKey(token), sessionValue, ttl);
        stringRedisTemplate.opsForValue().set(userSessionKey(userId), token, ttl);
    }

    /** Called on logout — removes token from allow-list. */
    public void removeSession(Long userId, String token) {
        stringRedisTemplate.delete(tokenKey(token));
        stringRedisTemplate.delete(userSessionKey(userId));
    }

    /** Called on login — enforces one active session per user. */
    public void revokeAllSessions(Long userId) {
        String activeToken = stringRedisTemplate.opsForValue().get(userSessionKey(userId));
        if (activeToken != null && !activeToken.isBlank()) {
            stringRedisTemplate.delete(tokenKey(activeToken));
        }
        stringRedisTemplate.delete(userSessionKey(userId));
    }

    private TokenClaims parseSessionValue(String token, String value) {
        String[] parts = value.split("\\" + SESSION_VALUE_SEPARATOR, SESSION_VALUE_PART_COUNT);
        if (parts.length != SESSION_VALUE_PART_COUNT) {
            throw new UnauthorizedException("Invalid token");
        }
        try {
            Long userId = Long.parseLong(parts[0]);
            UserRole role = UserRole.valueOf(parts[1]);
            UUID sessionId = UUID.fromString(parts[2]);
            return new TokenClaims(userId, token, role, sessionId);
        } catch (IllegalArgumentException exception) {
            throw new UnauthorizedException("Invalid token");
        }
    }

    private void validateTokenFormat(String token) {
        int expectedLength = jwtProperties.getTokenLength();
        if (token == null || token.length() != expectedLength || !token.matches("[0-9a-f]+")) {
            throw new UnauthorizedException("Invalid token");
        }
    }

    private String tokenKey(String token) {
        return jwtProperties.getRedisPrefix() + "token:" + token;
    }

    private String userSessionKey(Long userId) {
        return jwtProperties.getRedisPrefix() + "user:" + userId;
    }
}
