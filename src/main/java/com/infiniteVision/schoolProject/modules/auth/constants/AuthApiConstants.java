package com.infiniteVision.schoolProject.modules.auth.constants;

import com.infiniteVision.schoolProject.constants.ApiConstants;

/**
 * Auth module API paths.
 */
public final class AuthApiConstants {

    private AuthApiConstants() {
    }

    public static final String AUTH_BASE = ApiConstants.API_V1_PREFIX + "/auth";
    public static final String USERS = AUTH_BASE + "/users";
    public static final String FORGOT_PASSWORD = AUTH_BASE + "/forgot-password";
    public static final String VERIFY_OTP = AUTH_BASE + "/verify-otp";
    public static final String RESET_PASSWORD = AUTH_BASE + "/reset-password";
    public static final String LOGIN_SEND_OTP = AUTH_BASE + "/login/send-otp";
    public static final String LOGIN_VERIFY_OTP = AUTH_BASE + "/login/verify-otp";
    public static final String LOGIN_RESEND_OTP = AUTH_BASE + "/login/resend-otp";
}
