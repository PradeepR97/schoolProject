package com.infiniteVision.schoolProject.modules.auth.service.impl;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.auth.dto.request.ChangePasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.LoginResponseDTO;
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

        LocalDateTime sessionStarts = LocalDateTime.now();
        UUID sessionId = UUID.randomUUID();

        // Invalidate previous Redis + DB sessions (single active session per user)
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

        return LoginResponseDTO.builder()
                .token(token)
                .tokenType(TOKEN_TYPE)
                .expiresIn(jwtService.expirationSeconds())
                .user(toProfile(user))
                .build();
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
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            return userRepository.findByEmailAndDeletedFalse(request.getEmail().trim());
        }
        return userRepository.findByPhoneAndDeletedFalse(request.getPhone().trim());
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
