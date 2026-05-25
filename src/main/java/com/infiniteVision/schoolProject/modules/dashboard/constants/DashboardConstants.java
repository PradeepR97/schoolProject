package com.infiniteVision.schoolProject.modules.dashboard.constants;

/**
 * Dashboard module limits and cache key prefixes (Redis-ready).
 */
public final class DashboardConstants {

    private DashboardConstants() {}

    public static final int RECENT_ITEMS_LIMIT = 5;

    /** Redis cache name prefix when {@code @Cacheable} is enabled later. */
    public static final String CACHE_SUMMARY = "dashboard:summary";
}
