package com.infiniteVision.schoolProject.constants;

/**
 * Constants for REST API request/response logging.
 */
public final class LoggingConstants {

    private LoggingConstants() {
    }

    public static final String API_REQUEST_LOG_PREFIX = "API Request";
    public static final String API_RESPONSE_LOG_PREFIX = "API Response";

    public static final int MAX_LOG_BODY_CHARACTERS = 10_240;

    public static final String MASKED_VALUE = "***";
}
