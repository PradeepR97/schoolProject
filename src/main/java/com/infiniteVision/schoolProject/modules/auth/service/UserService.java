package com.infiniteVision.schoolProject.modules.auth.service;

import com.infiniteVision.schoolProject.modules.auth.dto.request.CreateUserRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.CreateUserResponseDTO;

/**
 * User lifecycle operations (creation, etc.) separate from login/logout in {@link AuthService}.
 */
public interface UserService {

    CreateUserResponseDTO createUser(CreateUserRequestDTO request);
}
