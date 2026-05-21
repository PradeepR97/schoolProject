package com.infiniteVision.schoolProject.modules.auth.service.impl;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.BusinessException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.auth.dto.request.CreateUserRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.CreateUserResponseDTO;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import com.infiniteVision.schoolProject.modules.auth.enums.UserStatus;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.auth.service.UserService;
import com.infiniteVision.schoolProject.modules.auth.util.UserDuplicateConstraintResolver;
import com.infiniteVision.schoolProject.modules.auth.validator.RoleAssignmentValidator;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User creation with role-based authorization enforced in the service layer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleAssignmentValidator roleAssignmentValidator;

    /**
     * Create a user when the caller is ADMIN or PRINCIPAL and may assign the requested role.
     * Password is BCrypt-hashed; new accounts default to ACTIVE.
     */
    @Override
    @Transactional
    public CreateUserResponseDTO createUser(CreateUserRequestDTO request) {
        AuthenticatedUser creator = currentUser();
        validateCreatorMayCreateUsers(creator.getRole());
        validateRoleAssignment(creator.getRole(), request.getRole());
        validateUniqueIdentifiers(request);

        User user = User.builder()
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone().trim())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .deleted(Boolean.FALSE)
                .build();

        User saved;
        try {
            saved = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            UserDuplicateConstraintResolver.toValidationException(exception)
                    .ifPresent(validationException -> {
                        throw validationException;
                    });
            throw exception;
        }
        log.info("User created id={} role={} by creator id={}", saved.getId(), saved.getRole(), creator.getUserId());

        return toResponse(saved);
    }

    /**
     * CORRESPONDENT cannot create users; ADMIN and PRINCIPAL may (subject to role matrix).
     */
    private void validateCreatorMayCreateUsers(UserRole creatorRole) {
        if (UserRole.CORRESPONDENT.equals(creatorRole)) {
            throw new BusinessException(MessageConstants.USER_CREATION_FORBIDDEN, HttpStatus.FORBIDDEN);
        }
    }

    /**
     * ADMIN may assign any role; PRINCIPAL may assign CORRESPONDENT only.
     */
    private void validateRoleAssignment(UserRole creatorRole, UserRole targetRole) {
        if (!roleAssignmentValidator.canAssignRole(creatorRole, targetRole)) {
            throw new BusinessException(MessageConstants.ROLE_ASSIGNMENT_FORBIDDEN, HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Reject duplicate username, email, or phone (aligned with DB unique constraints on all rows).
     */
    private void validateUniqueIdentifiers(CreateUserRequestDTO request) {
        List<String> errors = new ArrayList<>();
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();
        String phone = request.getPhone().trim();

        if (userRepository.existsByUsername(username)) {
            errors.add(MessageConstants.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            errors.add(MessageConstants.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByPhone(phone)) {
            errors.add(MessageConstants.PHONE_ALREADY_EXISTS);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    private CreateUserResponseDTO toResponse(User user) {
        return CreateUserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
