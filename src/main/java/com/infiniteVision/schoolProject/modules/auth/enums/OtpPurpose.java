package com.infiniteVision.schoolProject.modules.auth.enums;

/**
 * Distinguishes Redis OTP keys for first-login vs forgot-password flows.
 */
public enum OtpPurpose {

    FIRST_LOGIN("first_login"),
    FORGOT_PASSWORD("forgot");

    private final String redisKeySegment;

    OtpPurpose(String redisKeySegment) {
        this.redisKeySegment = redisKeySegment;
    }

    public String redisKeySegment() {
        return redisKeySegment;
    }
}
