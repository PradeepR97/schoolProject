package com.infiniteVision.schoolProject.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response after successful OTP verification: one-time token for password reset.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpResponseDTO {

    private String resetToken;
    private long expiresIn;
}
