package com.infiniteVision.schoolProject.modules.auth.service.impl;

import com.infiniteVision.schoolProject.config.OtpProperties;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.auth.enums.OtpPurpose;
import com.infiniteVision.schoolProject.modules.auth.service.OtpService;
import com.infiniteVision.schoolProject.security.JwtService;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Redis OTP storage: purpose-specific keys, resend cooldown, and password-reset tokens.
 * <p>
 * Mock mode ({@code otp.mock-enabled=true}) uses {@code otp.mock-value} (default {@code 123456}).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final String VALUE_SEPARATOR = "|";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OtpProperties otpProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtService jwtService;

    @Override
    public void sendOtp(OtpPurpose purpose, Long userId, String identifier) {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(cooldownKey(purpose, identifier)))) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.OTP_RESEND_TOO_SOON));
        }

        String otp = resolveOtpToStore();
        String payload = userId + VALUE_SEPARATOR + otp;
        Duration otpTtl = Duration.ofMinutes(otpProperties.getExpirationMinutes());
        Duration cooldownTtl = Duration.ofSeconds(otpProperties.getResendCooldownSeconds());

        stringRedisTemplate.opsForValue().set(otpKey(purpose, identifier), payload, otpTtl);
        stringRedisTemplate.opsForValue().set(cooldownKey(purpose, identifier), "1", cooldownTtl);

        log.info(
                "{} OTP sent for user id={}, identifier={}",
                purpose.name(),
                userId,
                maskIdentifier(identifier));
    }

    @Override
    public Long verifyOtp(OtpPurpose purpose, String identifier, String otp) {
        String stored = stringRedisTemplate.opsForValue().get(otpKey(purpose, identifier));
        if (stored == null || stored.isBlank()) {
            throw new UnauthorizedException(MessageConstants.OTP_EXPIRED);
        }

        String[] parts = stored.split("\\" + VALUE_SEPARATOR, 2);
        if (parts.length != 2) {
            stringRedisTemplate.delete(otpKey(purpose, identifier));
            throw new UnauthorizedException(MessageConstants.INVALID_OTP);
        }

        Long userId;
        try {
            userId = Long.parseLong(parts[0]);
        } catch (NumberFormatException exception) {
            stringRedisTemplate.delete(otpKey(purpose, identifier));
            throw new UnauthorizedException(MessageConstants.INVALID_OTP);
        }

        String expectedOtp = parts[1];
        if (!constantTimeEquals(expectedOtp, otp.trim())) {
            log.warn("{} OTP verification failed for identifier={}", purpose.name(), maskIdentifier(identifier));
            throw new UnauthorizedException(MessageConstants.INVALID_OTP);
        }

        stringRedisTemplate.delete(otpKey(purpose, identifier));
        return userId;
    }

    @Override
    public long otpExpirationSeconds() {
        return otpProperties.getExpirationMinutes() * 60L;
    }

    @Override
    public void sendForgotPasswordOtp(Long userId, String identifier) {
        sendOtp(OtpPurpose.FORGOT_PASSWORD, userId, identifier);
    }

    @Override
    public Long verifyForgotPasswordOtp(String identifier, String otp) {
        return verifyOtp(OtpPurpose.FORGOT_PASSWORD, identifier, otp);
    }

    @Override
    public String issueResetToken(Long userId) {
        String resetToken = jwtService.generateSessionToken();
        Duration ttl = Duration.ofMinutes(otpProperties.getResetTokenExpirationMinutes());
        stringRedisTemplate
                .opsForValue()
                .set(resetTokenKey(resetToken), String.valueOf(userId), ttl);
        log.info("Password reset token issued for user id={}", userId);
        return resetToken;
    }

    @Override
    public Long resolveResetToken(String resetToken) {
        if (resetToken == null || resetToken.isBlank()) {
            throw new UnauthorizedException(MessageConstants.RESET_TOKEN_INVALID);
        }
        String userIdValue = stringRedisTemplate.opsForValue().get(resetTokenKey(resetToken.trim()));
        if (userIdValue == null || userIdValue.isBlank()) {
            throw new UnauthorizedException(MessageConstants.RESET_TOKEN_INVALID);
        }
        try {
            return Long.parseLong(userIdValue);
        } catch (NumberFormatException exception) {
            throw new UnauthorizedException(MessageConstants.RESET_TOKEN_INVALID);
        }
    }

    @Override
    public void consumeResetToken(String resetToken) {
        if (resetToken != null && !resetToken.isBlank()) {
            stringRedisTemplate.delete(resetTokenKey(resetToken.trim()));
        }
    }

    @Override
    public long resetTokenExpirationSeconds() {
        return otpProperties.getResetTokenExpirationMinutes() * 60L;
    }

    private String resolveOtpToStore() {
        if (otpProperties.isMockEnabled()) {
            return otpProperties.getMockValue();
        }
        int bound = (int) Math.pow(10, otpProperties.getLength());
        int value = SECURE_RANDOM.nextInt(bound);
        return String.format("%0" + otpProperties.getLength() + "d", value);
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        byte[] expectedBytes = expected.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] actualBytes = actual.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return java.security.MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private String otpKey(OtpPurpose purpose, String identifier) {
        return otpProperties.getRedisPrefix() + purpose.redisKeySegment() + ":code:" + identifier;
    }

    private String cooldownKey(OtpPurpose purpose, String identifier) {
        return otpProperties.getRedisPrefix() + purpose.redisKeySegment() + ":cooldown:" + identifier;
    }

    private String resetTokenKey(String resetToken) {
        return otpProperties.getRedisPrefix() + "reset:" + resetToken;
    }

    private static String maskIdentifier(String identifier) {
        if (identifier == null || identifier.length() < 8) {
            return "***";
        }
        return identifier.substring(0, Math.min(6, identifier.length())) + "***";
    }
}
