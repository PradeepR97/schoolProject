package com.infiniteVision.schoolProject.modules.auth.service.impl;

import com.infiniteVision.schoolProject.common.audit.context.AuditParticipant;
import com.infiniteVision.schoolProject.common.audit.enums.AuditAction;
import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import java.util.Map;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.auth.dto.request.ChangePasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.ForgotPasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginSendOtpRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginVerifyOtpRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.ResetPasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.enums.OtpPurpose;
import com.infiniteVision.schoolProject.modules.auth.dto.request.VerifyOtpRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.LoginResponseDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.VerifyOtpResponseDTO;
import com.infiniteVision.schoolProject.modules.auth.service.OtpService;
import com.infiniteVision.schoolProject.modules.auth.util.AuthIdentifierUtil;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.entity.UserSession;
import com.infiniteVision.schoolProject.modules.auth.enums.UserStatus;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.auth.repository.UserSessionRepository;
import com.infiniteVision.schoolProject.modules.auth.service.AuthService;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import com.infiniteVision.schoolProject.security.JwtService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link AuthService}.
 * <p>
 * <b>Login flow:</b> validate identifiers → load user → verify password → end prior DB/Redis sessions
 * → insert {@code user_sessions} row → store Redis → update {@code last_login} → response.
 * <p>
 * <b>Logout flow:</b> set {@code session_ends} in MySQL → remove Redis session keys.
 * <p>
 * <b>Change password flow:</b> validate confirmation → verify current BCrypt → save new hash
 * → revoke all DB/Redis sessions (re-login required).
 * <p>
 * <b>Forgot password flow:</b> send OTP (Redis) → verify OTP → issue reset token → BCrypt reset
 * → revoke sessions. Mock OTP {@code 123456} when {@code otp.mock-enabled=true}.
 * <p>
 * <b>First login flow:</b> password OK but {@code otp_verified=false} → send OTP, no Bearer token
 * → verify OTP → set {@code otp_verified=true} → issue session token.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final AuditService auditService;

    /**
     * Authenticate user by email or phone, verify BCrypt password, issue opaque Bearer token,
     * persist session in {@code user_sessions}, store Redis allow-list, revoke prior sessions.
     */
    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        validateLoginIdentifiers(request);

        User user = findUser(request)
                .orElseThrow(() -> new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED));

        if (!UserStatus.ACTIVE.equals(user.getStatus())
                || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed for user id={}", user.getId());
            throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
        }

        if (requiresFirstLoginOtp(user)) {
            String identifier = AuthIdentifierUtil.normalizeIdentifier(request.getEmail(), request.getPhone());
            otpService.sendOtp(OtpPurpose.FIRST_LOGIN, user.getId(), identifier);
            log.info("First-login OTP required for user id={}", user.getId());
            return LoginResponseDTO.builder()
                    .otpRequired(Boolean.TRUE)
                    .otpExpiresIn(otpService.otpExpirationSeconds())
                    .user(toProfile(user))
                    .build();
        }

        return completeLoginSession(user);
    }

    /**
     * End the current session in MySQL ({@code session_ends}) and remove Redis allow-list entry.
     */
    @Override
    @Transactional
    public void logout() {
        AuthenticatedUser principal = currentUser();
        LocalDateTime sessionEnds = LocalDateTime.now();

        userSessionRepository.endSession(principal.getSessionId(), sessionEnds);
        jwtService.removeSession(principal.getUserId(), principal.getSessionToken());

        auditService.logAuthEvent(
                AuditEntityType.USER_SESSION,
                principal.getUserId(),
                AuditAction.LOGOUT,
                Map.of(
                        "userId", principal.getUserId(),
                        "sessionId", principal.getSessionId().toString(),
                        "username", principal.getUsername()),
                AuditParticipant.of(principal.getUserId(), principal.getSessionId()),
                "User logout");

        log.info("Logout successful for user id={}, sessionId={}", principal.getUserId(), principal.getSessionId());
    }

    /**
     * Change password for the authenticated user: verify current password, persist new BCrypt hash,
     * end all active sessions in MySQL and Redis so existing Bearer tokens are invalidated.
     */
    @Override
    @Transactional
    public void changePassword(ChangePasswordRequestDTO request) {
        AuthenticatedUser principal = currentUser();

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.NEW_PASSWORD_MISMATCH));
        }

        User user = userRepository.findByIdAndDeletedFalse(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Change password failed for user id={}", user.getId());
            throw new UnauthorizedException(MessageConstants.CURRENT_PASSWORD_INCORRECT);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.NEW_PASSWORD_SAME_AS_CURRENT));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        // Flush before session bulk-update: endAllActiveSessionsForUser uses clearAutomatically=true
        // and would otherwise discard this password change from the persistence context.
        userRepository.saveAndFlush(user);

        LocalDateTime sessionEnds = LocalDateTime.now();
        jwtService.revokeAllSessions(user.getId());
        userSessionRepository.endAllActiveSessionsForUser(user.getId(), sessionEnds);

        log.info("Password changed for user id={}", user.getId());
    }

    /**
     * Sends forgot-password OTP for active users; always completes without revealing account existence.
     */
    @Override
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        AuthIdentifierUtil.validateForgotPasswordIdentifiers(request);
        String identifier =
                AuthIdentifierUtil.normalizeIdentifier(request.getEmail(), request.getPhone());

        findUserByIdentifier(request.getEmail(), request.getPhone())
                .filter(user -> UserStatus.ACTIVE.equals(user.getStatus()))
                .ifPresent(user -> otpService.sendForgotPasswordOtp(user.getId(), identifier));

        log.info("Forgot-password OTP request processed for identifier={}", identifier.substring(0, Math.min(6, identifier.length())) + "***");
    }

    /**
     * Validates OTP in Redis and returns a short-lived reset token for {@link #resetPassword}.
     */
    @Override
    public VerifyOtpResponseDTO verifyOtp(VerifyOtpRequestDTO request) {
        AuthIdentifierUtil.validateVerifyOtpIdentifiers(request);
        String identifier =
                AuthIdentifierUtil.normalizeIdentifier(request.getEmail(), request.getPhone());

        Long userId = otpService.verifyForgotPasswordOtp(identifier, request.getOtp());
        userRepository
                .findByIdAndDeletedFalse(userId)
                .filter(user -> UserStatus.ACTIVE.equals(user.getStatus()))
                .orElseThrow(() -> new UnauthorizedException(MessageConstants.OTP_VERIFICATION_FAILED));

        String resetToken = otpService.issueResetToken(userId);
        return VerifyOtpResponseDTO.builder()
                .resetToken(resetToken)
                .expiresIn(otpService.resetTokenExpirationSeconds())
                .build();
    }

    /**
     * Updates password from reset token; invalidates token and all login sessions.
     */
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.NEW_PASSWORD_MISMATCH));
        }

        Long userId = otpService.resolveResetToken(request.getResetToken());
        User user = userRepository
                .findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new UnauthorizedException(MessageConstants.RESET_TOKEN_INVALID));

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new UnauthorizedException(MessageConstants.RESET_TOKEN_INVALID);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.NEW_PASSWORD_SAME_AS_CURRENT));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.saveAndFlush(user);

        LocalDateTime sessionEnds = LocalDateTime.now();
        jwtService.revokeAllSessions(user.getId());
        userSessionRepository.endAllActiveSessionsForUser(user.getId(), sessionEnds);
        otpService.consumeResetToken(request.getResetToken());

        log.info("Password reset successful for user id={}", user.getId());
    }

    /**
     * Sends first-login OTP when the account has not completed verification.
     */
    @Override
    public void sendLoginOtp(LoginSendOtpRequestDTO request) {
        User user = resolveUserPendingFirstLoginOtp(request.getEmail(), request.getPhone());
        String identifier = AuthIdentifierUtil.normalizeIdentifier(request.getEmail(), request.getPhone());
        otpService.sendOtp(OtpPurpose.FIRST_LOGIN, user.getId(), identifier);
        log.info("First-login OTP send requested for user id={}", user.getId());
    }

    /**
     * Resend first-login OTP (same rules as send; 30-second cooldown enforced in Redis).
     */
    @Override
    public void resendLoginOtp(LoginSendOtpRequestDTO request) {
        sendLoginOtp(request);
    }

    /**
     * Verifies first-login OTP, marks user verified, and completes login with Bearer token.
     */
    @Override
    @Transactional
    public LoginResponseDTO verifyLoginOtp(LoginVerifyOtpRequestDTO request) {
        AuthIdentifierUtil.validateLoginVerifyOtpIdentifiers(request);
        String identifier =
                AuthIdentifierUtil.normalizeIdentifier(request.getEmail(), request.getPhone());

        Long userId = otpService.verifyOtp(OtpPurpose.FIRST_LOGIN, identifier, request.getOtp());
        User user = userRepository
                .findByIdAndDeletedFalse(userId)
                .filter(account -> UserStatus.ACTIVE.equals(account.getStatus()))
                .orElseThrow(() -> new UnauthorizedException(MessageConstants.INVALID_OTP));

        if (!identifierMatchesUser(user, request.getEmail(), request.getPhone())) {
            throw new UnauthorizedException(MessageConstants.INVALID_OTP);
        }

        user.setOtpVerified(Boolean.TRUE);
        userRepository.saveAndFlush(user);

        log.info("First-login OTP verified for user id={}", user.getId());
        return completeLoginSession(user);
    }

    /**
     * Creates DB/Redis session and returns login payload with Bearer token.
     */
    private LoginResponseDTO completeLoginSession(User user) {
        LocalDateTime sessionStarts = LocalDateTime.now();
        UUID sessionId = UUID.randomUUID();

        jwtService.revokeAllSessions(user.getId());
        userSessionRepository.endAllActiveSessionsForUser(user.getId(), sessionStarts);

        userSessionRepository.save(UserSession.builder()
                .sessionId(sessionId)
                .userId(user.getId())
                .sessionStarts(sessionStarts)
                .deleted(Boolean.FALSE)
                .build());

        String token = jwtService.generateSessionToken();
        jwtService.storeSession(user.getId(), token, user.getRole(), sessionId);

        user.setLastLogin(sessionStarts);
        userRepository.saveAndFlush(user);
        log.info("Login successful for user id={}, sessionId={}", user.getId(), sessionId);

        auditService.logAuthEvent(
                AuditEntityType.USER_SESSION,
                user.getId(),
                AuditAction.LOGIN,
                Map.of(
                        "userId", user.getId(),
                        "sessionId", sessionId.toString(),
                        "username", user.getUsername(),
                        "role", user.getRole().name()),
                AuditParticipant.of(user.getId(), sessionId),
                "User login");

        return LoginResponseDTO.builder()
                .token(token)
                .tokenType(TOKEN_TYPE)
                .expiresIn(jwtService.expirationSeconds())
                .otpRequired(Boolean.FALSE)
                .user(toProfile(user))
                .build();
    }

    private User resolveUserPendingFirstLoginOtp(String email, String phone) {
        AuthIdentifierUtil.validateLoginSendOtpIdentifiers(
                LoginSendOtpRequestDTO.builder().email(email).phone(phone).build());
        User user = findUserByIdentifier(email, phone)
                .orElseThrow(() -> new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED));
        if (!UserStatus.ACTIVE.equals(user.getStatus()) || !requiresFirstLoginOtp(user)) {
            throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
        }
        return user;
    }

    private static boolean requiresFirstLoginOtp(User user) {
        return !Boolean.TRUE.equals(user.getOtpVerified());
    }

    private static boolean identifierMatchesUser(User user, String email, String phone) {
        if (AuthIdentifierUtil.hasEmail(email)) {
            return user.getEmail().equalsIgnoreCase(email.trim());
        }
        return user.getPhone().equals(phone.trim());
    }

    private void validateLoginIdentifiers(LoginRequestDTO request) {
        boolean hasEmail = request.getEmail() != null && !request.getEmail().isBlank();
        boolean hasPhone = request.getPhone() != null && !request.getPhone().isBlank();
        if (hasEmail == hasPhone) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED,
                    List.of("Provide exactly one of email or phone"));
        }
    }

    private Optional<User> findUser(LoginRequestDTO request) {
        return findUserByIdentifier(request.getEmail(), request.getPhone());
    }

    private Optional<User> findUserByIdentifier(String email, String phone) {
        if (AuthIdentifierUtil.hasEmail(email)) {
            return userRepository.findByEmailAndDeletedFalse(email.trim().toLowerCase());
        }
        return userRepository.findByPhoneAndDeletedFalse(phone.trim());
    }

    private LoginResponseDTO.UserProfile toProfile(User user) {
        return LoginResponseDTO.UserProfile.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .otpVerified(user.getOtpVerified())
                .build();
    }

    private AuthenticatedUser currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
