package com.infiniteVision.schoolProject.modules.auth.dto.response;

import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import com.infiniteVision.schoolProject.modules.auth.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Login response: single JWT and user profile.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String tokenType;
    private long expiresIn;
    /** {@code true} when password is valid but first-login OTP is still required (no token issued). */
    private Boolean otpRequired;
    private Long otpExpiresIn;
    private UserProfile user;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfile {
        private Long id;
        private String username;
        private String fullName;
        private String email;
        private String phone;
        private UserRole role;
        private UserStatus status;
        private Boolean otpVerified;
    }
}
