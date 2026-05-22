package com.infiniteVision.schoolProject.modules.auth.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.modules.auth.constants.AuthApiConstants;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.auth.dto.request.ChangePasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.LoginResponseDTO;
import com.infiniteVision.schoolProject.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for authentication. Business logic lives in {@link AuthService}.
 * <p>
 * Login is public; logout requires Bearer token (validated by JWT filter).
 */
@RestController
@RequestMapping(value = AuthApiConstants.AUTH_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** POST /api/v1/auth/login — email or phone + password. */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOGIN_SUCCESS, authService.login(request)));
    }

    /** POST /api/v1/auth/logout — invalidates current token in Redis. */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOGOUT_SUCCESS));
    }

    /** POST /api/v1/auth/change-password — updates password and revokes all sessions. */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.PASSWORD_CHANGED_SUCCESS));
    }
}
