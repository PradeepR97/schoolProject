package com.infiniteVision.schoolProject.modules.auth.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.modules.auth.constants.AuthApiConstants;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.auth.dto.request.ChangePasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.ForgotPasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginSendOtpRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginVerifyOtpRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.ResetPasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.VerifyOtpRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.LoginResponseDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.VerifyOtpResponseDTO;
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
        LoginResponseDTO data = authService.login(request);
        String message = Boolean.TRUE.equals(data.getOtpRequired())
                ? MessageConstants.LOGIN_OTP_REQUIRED
                : MessageConstants.LOGIN_SUCCESS;
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    /** POST /api/v1/auth/login/send-otp — send first-login OTP (public). */
    @PostMapping("/login/send-otp")
    public ResponseEntity<ApiResponse<Void>> sendLoginOtp(@Valid @RequestBody LoginSendOtpRequestDTO request) {
        authService.sendLoginOtp(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOGIN_OTP_SENT_SUCCESS));
    }

    /** POST /api/v1/auth/login/resend-otp — resend first-login OTP (public). */
    @PostMapping("/login/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendLoginOtp(@Valid @RequestBody LoginSendOtpRequestDTO request) {
        authService.resendLoginOtp(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOGIN_OTP_SENT_SUCCESS));
    }

    /** POST /api/v1/auth/login/verify-otp — verify OTP and complete login (public). */
    @PostMapping("/login/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> verifyLoginOtp(
            @Valid @RequestBody LoginVerifyOtpRequestDTO request) {
        LoginResponseDTO data = authService.verifyLoginOtp(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FIRST_LOGIN_OTP_VERIFIED_SUCCESS, data));
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

    /** POST /api/v1/auth/forgot-password — send OTP to email or phone (public). */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.OTP_SENT_SUCCESS));
    }

    /** POST /api/v1/auth/verify-otp — validate OTP and return reset token (public). */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<VerifyOtpResponseDTO>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequestDTO request) {
        VerifyOtpResponseDTO data = authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.OTP_VERIFIED_SUCCESS, data));
    }

    /** POST /api/v1/auth/reset-password — set new password using reset token (public). */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.PASSWORD_RESET_SUCCESS));
    }
}
