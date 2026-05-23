package com.infiniteVision.schoolProject.constants;

/**
 * Constants for change-audit logging.
 */
public final class AuditConstants {

    private AuditConstants() {
    }

    /** Optional client header; generated if absent. */
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    public static final int REQUEST_ID_MAX_LENGTH = 64;
    public static final int API_PATH_MAX_LENGTH = 255;
    public static final int IP_ADDRESS_MAX_LENGTH = 45;
    public static final int REMARKS_MAX_LENGTH = 500;
    public static final int PERFORMED_BY_MAX_LENGTH = 100;
}
