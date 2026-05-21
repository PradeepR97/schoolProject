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
}
