package com.infiniteVision.schoolProject.common.audit.context;

/**
 * HTTP metadata captured per request for audit rows.
 */
public record AuditRequestContext(String requestId, String ipAddress, String apiPath) {

    public static AuditRequestContext empty() {
        return new AuditRequestContext(null, null, null);
    }
}
