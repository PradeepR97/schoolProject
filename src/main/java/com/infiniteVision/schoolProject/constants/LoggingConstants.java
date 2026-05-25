package com.infiniteVision.schoolProject.constants;

/**
 * Constants for REST API request/response logging.
 */
public final class LoggingConstants {

    private LoggingConstants() {
    }

    public static final String API_REQUEST_LOG_PREFIX = "API Request";
    public static final String API_RESPONSE_LOG_PREFIX = "API Response";

    /** Logged instead of binary Excel/PDF bytes for file download endpoints. */
    public static final String FILE_DOWNLOADED_MESSAGE = "File downloaded successfully";

    public static final int MAX_LOG_BODY_CHARACTERS = 10_240;

    public static final String MASKED_VALUE = "***";
}
