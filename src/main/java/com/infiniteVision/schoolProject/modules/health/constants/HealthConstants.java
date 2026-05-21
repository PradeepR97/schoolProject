package com.infiniteVision.schoolProject.modules.health.constants;

/**
 * Health check status values and dependency error detail messages.
 */
public final class HealthConstants {

    private HealthConstants() {
    }

    public static final String STATUS_UP = "UP";
    public static final String STATUS_DOWN = "DOWN";
    public static final String CONNECTED = "CONNECTED";
    public static final String DISCONNECTED = "DISCONNECTED";

    public static final String HEALTH_CHECK_SUCCESS = "Application is running successfully";
    public static final String DATABASE_CONNECTION_FAILED = "Database connection failed";
    public static final String REDIS_CONNECTION_FAILED = "Redis connection failed";
    public static final String MYSQL_SERVICE_UNAVAILABLE = "MySQL service unavailable";
    public static final String REDIS_SERVICE_UNAVAILABLE = "Redis service unavailable";
}
