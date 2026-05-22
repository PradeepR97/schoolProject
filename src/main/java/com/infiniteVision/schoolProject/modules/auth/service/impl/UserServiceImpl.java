package com.infiniteVision.schoolProject.modules.auth.service.impl;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.BusinessException;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.auth.dto.request.CreateUserRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.request.UpdateUserRequestDTO;
import com.infiniteVision.schoolProject.modules.auth.dto.response.CreateUserResponseDTO;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import com.infiniteVision.schoolProject.modules.auth.enums.UserStatus;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.auth.repository.UserSessionRepository;
import com.infiniteVision.schoolProject.modules.auth.service.UserService;
import com.infiniteVision.schoolProject.modules.auth.util.UserDuplicateConstraintResolver;
import com.infiniteVision.schoolProject.modules.auth.validator.RoleAssignmentValidator;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import com.infiniteVision.schoolProject.security.JwtService;
import java.time.LocalDateTime;
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
 * User management with role-based authorization enforced in the service layer.
 * <p>
 * List, get, update, and delete require ADMIN or PRINCIPAL; CORRESPONDENT is rejected in the service layer.
 * Audit fields ({@code created_by}, {@code updated_by}, {@code deleted_by}) store the caller's user ID via
 * JPA auditing and {@link com.infiniteVision.schoolProject.common.entity.BaseEntity#markDeleted(Long)}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleAssignmentValidator roleAssignmentValidator;
    private final JwtService jwtService;

    /**
     * Load all non-deleted users ordered by id. CORRESPONDENT callers are rejected.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CreateUserResponseDTO> listUsers() {
        AuthenticatedUser caller = currentUser();
        validateCallerMayManageUsers(caller.getRole());

        List<CreateUserResponseDTO> users = userRepository.findAllByDeletedFalseOrderByIdAsc().stream()
                .map(this::toResponse)
                .toList();
        log.info("Users listed count={} by caller id={}", users.size(), caller.getUserId());
        return users;
    }

    /**
     * Load one non-deleted user by id. CORRESPONDENT callers are rejected.
     */
    @Override
    @Transactional(readOnly = true)
    public CreateUserResponseDTO getUserById(Long id) {
        AuthenticatedUser caller = currentUser();
        validateCallerMayManageUsers(caller.getRole());

        User user = findActiveUserOrThrow(id);
        log.info("User retrieved id={} by caller id={}", id, caller.getUserId());
        return toResponse(user);
    }

    /**
     * Create a user when the caller is ADMIN or PRINCIPAL and may assign the requested role.
     * Password is BCrypt-hashed; new accounts default to ACTIVE; {@code created_by} set by JPA auditing.
     */
    @Override
    @Transactional
    public CreateUserResponseDTO createUser(CreateUserRequestDTO request) {
        AuthenticatedUser creator = currentUser();
        validateCallerMayManageUsers(creator.getRole());
        validateRoleAssignment(creator.getRole(), request.getRole());
        validateUniqueIdentifiersForCreate(request);

        User user = User.builder()
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone().trim())
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .otpVerified(Boolean.FALSE)
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
     * Apply partial updates to an active user; {@code updated_by} set by JPA auditing on save.
     */
    @Override
    @Transactional
    public CreateUserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        validateCallerMayManageUsers(caller.getRole());
        validateUpdateRequestHasFields(request);

        User user = findActiveUserOrThrow(id);

        if (request.getRole() != null) {
            validateRoleAssignment(caller.getRole(), request.getRole());
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail().trim().toLowerCase());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            user.setPhone(request.getPhone().trim());
        }

        validateUniqueIdentifiersForUpdate(id, user);

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
        log.info("User updated id={} by caller id={}", saved.getId(), caller.getUserId());
        return toResponse(saved);
    }

    /**
     * Soft-delete user: set deleted flag and audit fields, set INACTIVE, end DB sessions, revoke Redis tokens.
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        AuthenticatedUser caller = currentUser();
        validateCallerMayManageUsers(caller.getRole());

        if (caller.getUserId().equals(id)) {
            throw new BusinessException(MessageConstants.USER_DELETE_SELF_FORBIDDEN, HttpStatus.BAD_REQUEST);
        }

        User user = findActiveUserOrThrow(id);

        if (UserRole.ADMIN.equals(user.getRole())
                && userRepository.countByRoleAndDeletedFalse(UserRole.ADMIN) <= 1) {
            throw new BusinessException(MessageConstants.LAST_ADMIN_CANNOT_DELETE, HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        user.softDelete(caller.getUserId());

        userSessionRepository.endAllActiveSessionsForUser(id, now);
        jwtService.revokeAllSessions(id);
        userRepository.save(user);

        log.info("User soft-deleted id={} by caller id={}", id, caller.getUserId());
    }

    /**
     * CORRESPONDENT cannot manage users; ADMIN and PRINCIPAL may.
     */
    private void validateCallerMayManageUsers(UserRole callerRole) {
        if (UserRole.CORRESPONDENT.equals(callerRole)) {
            throw new BusinessException(MessageConstants.USER_MANAGEMENT_FORBIDDEN, HttpStatus.FORBIDDEN);
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

    private void validateUpdateRequestHasFields(UpdateUserRequestDTO request) {
        boolean hasField = (request.getFullName() != null && !request.getFullName().isBlank())
                || (request.getEmail() != null && !request.getEmail().isBlank())
                || (request.getPhone() != null && !request.getPhone().isBlank())
                || request.getRole() != null
                || request.getStatus() != null;
        if (!hasField) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.USER_UPDATE_EMPTY));
        }
    }

    /**
     * Reject duplicate username, email, or phone on create (DB unique constraints on all rows).
     */
    private void validateUniqueIdentifiersForCreate(CreateUserRequestDTO request) {
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

    /**
     * Reject duplicate email or phone on update, excluding the current user id.
     */
    private void validateUniqueIdentifiersForUpdate(Long id, User user) {
        List<String> errors = new ArrayList<>();
        if (userRepository.existsByEmailAndIdNot(user.getEmail(), id)) {
            errors.add(MessageConstants.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByPhoneAndIdNot(user.getPhone(), id)) {
            errors.add(MessageConstants.PHONE_ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    private User findActiveUserOrThrow(Long id) {
        return userRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.USER_NOT_FOUND));
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
                .otpVerified(user.getOtpVerified())
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
