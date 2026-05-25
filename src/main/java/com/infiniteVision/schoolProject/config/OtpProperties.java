package com.infiniteVision.schoolProject.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OTP and password-reset token settings (Redis-backed).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "otp")
public class OtpProperties {

    private int length = 6;
    private int expirationMinutes = 5;
    private int resendCooldownSeconds = 30;
    private int resetTokenExpirationMinutes = 15;
    private boolean mockEnabled = true;
    private String mockValue = "123456";
    private String redisPrefix = "otp:";
}
