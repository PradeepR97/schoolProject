package com.infiniteVision.schoolProject.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Send or resend first-login OTP: provide email or phone (validated in service).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSendOtpRequestDTO {

    @Email(message = "Email must be a valid address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(min = 10, max = 20, message = "Phone must be between 10 and 20 characters")
    private String phone;
}
