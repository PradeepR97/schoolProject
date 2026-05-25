package com.infiniteVision.schoolProject.common.audit.context;

/**
 * Thread-local holder for {@link AuditRequestContext} populated by {@link
 * com.infiniteVision.schoolProject.common.filter.AuditRequestContextFilter}.
 */
public final class AuditRequestContextHolder {

    private static final ThreadLocal<AuditRequestContext> CONTEXT = new ThreadLocal<>();

    private AuditRequestContextHolder() {
    }

    public static void set(AuditRequestContext context) {
        CONTEXT.set(context);
    }

    public static AuditRequestContext get() {
        AuditRequestContext context = CONTEXT.get();
        return context != null ? context : AuditRequestContext.empty();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
