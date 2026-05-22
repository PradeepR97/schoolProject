package com.infiniteVision.schoolProject.modules.auth.service;

import com.infiniteVision.schoolProject.modules.auth.dto.request.ChangePasswordRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.LoginRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.LoginResponseDTO;

/**
 * Authentication use cases for the School ERP API.
 * <p>
 * Implementations validate credentials against MySQL, issue a single opaque Bearer token per login,
 * persist sessions in {@code user_sessions}, and manage allow-list entries in Redis.
 */
public interface AuthService {

    /**
     * Authenticate with email <b>or</b> phone plus password.
     *
     * @param request login payload; exactly one of {@code email} or {@code phone} required
     * @return Bearer token, expiry in seconds, and safe user profile (password never included)
     * @throws com.infiniteVision.schoolProject.exception.ValidationException
     *         if both or neither identifier is provided
     * @throws com.infiniteVision.schoolProject.exception.UnauthorizedException
     *         if credentials are invalid or the account is not {@code ACTIVE}
     */
    LoginResponseDTO login(LoginRequestDTO request);

    /**
     * End the current session in {@code user_sessions} and remove the Redis allow-list entry.
     * <p>
     * Requires {@code Authorization: Bearer &lt;token&gt;}. The token must already be
     * validated by {@link com.infiniteVision.schoolProject.security.JwtAuthenticationFilter}.
     */
    void logout();

    /**
     * Change password for the currently authenticated user.
     * <p>
     * Verifies {@code currentPassword}, persists BCrypt hash for {@code newPassword},
     * and revokes all active sessions (client must log in again).
     *
     * @param request current, new, and confirmation passwords
     * @throws com.infiniteVision.schoolProject.exception.ValidationException
     *         if confirmation mismatch or new password equals current
     * @throws com.infiniteVision.schoolProject.exception.UnauthorizedException
     *         if not authenticated or current password is wrong
     * @throws com.infiniteVision.schoolProject.exception.ResourceNotFoundException
     *         if the authenticated user record is missing or soft-deleted
     */
    void changePassword(ChangePasswordRequestDTO request);
}
