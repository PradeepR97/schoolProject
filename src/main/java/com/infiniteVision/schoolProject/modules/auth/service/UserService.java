package com.infiniteVision.schoolProject.modules.auth.service;

import com.infiniteVision.schoolProject.modules.auth.dto.request.CreateUserRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.UpdateUserRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.CreateUserResponseDTO;
import java.util.List;

/**
 * User lifecycle operations (list, get, create, update, soft delete) separate from login/logout in
 * {@link AuthService}.
 */
public interface UserService {

    /**
     * Returns all active (non-deleted) users. Caller must be ADMIN or PRINCIPAL.
     *
     * @return user profiles without passwords
     */
    List<CreateUserResponseDTO> listUsers();

    /**
     * Returns one active user by id. Caller must be ADMIN or PRINCIPAL.
     *
     * @param id user primary key
     * @return user profile without password
     */
    CreateUserResponseDTO getUserById(Long id);

    CreateUserResponseDTO createUser(CreateUserRequestDTO request);

    /**
     * Updates profile fields for an active user. Caller must be ADMIN or PRINCIPAL.
     *
     * @param id user primary key
     * @param request partial fields to apply
     * @return updated user profile
     */
    CreateUserResponseDTO updateUser(Long id, UpdateUserRequestDTO request);

    /**
     * Soft-deletes a user (flag only), revokes sessions, and records audit fields. Caller must be ADMIN or PRINCIPAL.
     *
     * @param id user primary key
     */
    void deleteUser(Long id);
}
