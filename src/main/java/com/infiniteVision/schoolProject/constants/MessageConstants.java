package com.infiniteVision.schoolProject.constants;

/**
 * Centralized API and error messages. Do not hardcode these strings in handlers or controllers.
 */
public final class MessageConstants {

    private MessageConstants() {
    }

    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String LOGOUT_SUCCESS = "Logout successful";
    public static final String BUSINESS_ERROR = "Request could not be processed";
    public static final String DATA_INTEGRITY_VIOLATION = "Data conflict or constraint violation";
    public static final String DATABASE_ERROR = "Database operation failed";
    public static final String REDIS_UNAVAILABLE = "Cache service temporarily unavailable";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred";
}
