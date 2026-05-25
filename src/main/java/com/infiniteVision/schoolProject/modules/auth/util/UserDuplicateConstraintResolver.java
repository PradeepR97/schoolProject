package com.infiniteVision.schoolProject.modules.auth.util;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Maps user-table unique constraint violations to validation errors for create-user flows.
 */
public final class UserDuplicateConstraintResolver {

    private static final String UK_USERNAME = "uk_users_username";
    private static final String UK_EMAIL = "uk_users_email";
    private static final String UK_PHONE = "uk_users_phone";

    private UserDuplicateConstraintResolver() {
    }

    /**
     * If the exception is a known users-table duplicate key, throws {@link ValidationException}; otherwise empty.
     */
    public static Optional<ValidationException> toValidationException(DataIntegrityViolationException exception) {
        String message = rootMessage(exception).toLowerCase(Locale.ROOT);
        if (message.isBlank()) {
            return Optional.empty();
        }

        List<String> errors = new ArrayList<>();
        if (message.contains(UK_USERNAME)) {
            errors.add(MessageConstants.USERNAME_ALREADY_EXISTS);
        }
        if (message.contains(UK_EMAIL)) {
            errors.add(MessageConstants.EMAIL_ALREADY_EXISTS);
        }
        if (message.contains(UK_PHONE)) {
            errors.add(MessageConstants.PHONE_ALREADY_EXISTS);
        }

        if (errors.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ValidationException(MessageConstants.VALIDATION_FAILED, errors));
    }

    private static String rootMessage(DataIntegrityViolationException exception) {
        Throwable cause = exception.getMostSpecificCause();
        return cause != null && cause.getMessage() != null ? cause.getMessage() : exception.getMessage();
    }
}
