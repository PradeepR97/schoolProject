package com.infiniteVision.schoolProject.exception;

import java.util.Collections;
import java.util.List;

/**
 * Application-level validation failure (distinct from Bean Validation on DTOs).
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(String message) {
        this(message, Collections.emptyList());
    }

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors == null ? Collections.emptyList() : List.copyOf(errors);
    }

    public List<String> getErrors() {
        return errors;
    }
}
