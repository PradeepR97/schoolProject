package com.infiniteVision.schoolProject.modules.auth.dto.response;

import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import com.infiniteVision.schoolProject.modules.auth.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * User profile returned after successful creation (password never included).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserResponseDTO {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status;
}
