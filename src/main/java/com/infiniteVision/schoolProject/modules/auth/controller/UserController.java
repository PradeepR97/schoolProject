package com.infiniteVision.schoolProject.modules.auth.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.auth.constants.AuthApiConstants;
import com.infiniteVision.schoolProject.modules.auth.dto.request.CreateUserRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.CreateUserResponseDTO;
import com.infiniteVision.schoolProject.modules.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authenticated user management. Creation requires ADMIN or PRINCIPAL (see {@link UserService} for role matrix).
 */
@RestController
@RequestMapping(value = AuthApiConstants.USERS, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
}
