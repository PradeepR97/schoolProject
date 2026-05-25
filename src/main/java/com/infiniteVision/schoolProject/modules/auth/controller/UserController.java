package com.infiniteVision.schoolProject.modules.auth.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.auth.constants.AuthApiConstants;
import com.infiniteVision.schoolProject.modules.auth.dto.request.CreateUserRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.UpdateUserRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.CreateUserResponseDTO;
import com.infiniteVision.schoolProject.modules.auth.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authenticated user management. All endpoints require ADMIN or PRINCIPAL (see {@link UserService}).
 */
@RestController
@RequestMapping(value = AuthApiConstants.USERS, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/v1/auth/users — list all active users (Bearer token required).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<CreateUserResponseDTO>>> listUsers() {
        List<CreateUserResponseDTO> users = userService.listUsers();
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.USERS_LISTED_SUCCESS, users));
    }

    /**
     * GET /api/v1/auth/users/{id} — get one active user (Bearer token required).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<CreateUserResponseDTO>> getUserById(@PathVariable Long id) {
        CreateUserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.USER_RETRIEVED_SUCCESS, user));
    }

    /**
     * POST /api/v1/auth/users — create user (Bearer token required).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<CreateUserResponseDTO>> createUser(
            @Valid @RequestBody CreateUserRequestDTO request) {
        CreateUserResponseDTO created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.USER_CREATED_SUCCESS, created));
    }

    /**
     * PUT /api/v1/auth/users/{id} — update user (Bearer token required).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<CreateUserResponseDTO>> updateUser(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO request) {
        CreateUserResponseDTO updated = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.USER_UPDATED_SUCCESS, updated));
    }

    /**
     * DELETE /api/v1/auth/users/{id} — soft-delete user (Bearer token required).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.USER_DELETED_SUCCESS));
    }
}
