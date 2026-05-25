package com.infiniteVision.schoolProject.modules.auth.service;

import com.infiniteVision.schoolProject.modules.auth.enums.OtpPurpose;

/**
 * Redis-backed OTP and password-reset token management.
 */
public interface OtpService {

    /**
     * Stores OTP for the given purpose and applies resend cooldown.
     */
    void sendOtp(OtpPurpose purpose, Long userId, String identifier);

    /**
     * Validates OTP and returns the associated user id. Consumes the OTP key on success.
     */
    Long verifyOtp(OtpPurpose purpose, String identifier, String otp);

    /** OTP TTL in seconds (for API {@code otpExpiresIn}). */
    long otpExpirationSeconds();

    void sendForgotPasswordOtp(Long userId, String identifier);

    Long verifyForgotPasswordOtp(String identifier, String otp);

    String issueResetToken(Long userId);

    Long resolveResetToken(String resetToken);

    void consumeResetToken(String resetToken);

    long resetTokenExpirationSeconds();
}
