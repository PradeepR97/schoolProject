package com.infiniteVision.schoolProject.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for authenticated user password change.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {

    @NotBlank(message = "Current password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String newPassword;

    @NotBlank(message = "Confirm new password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String confirmNewPassword;
}
