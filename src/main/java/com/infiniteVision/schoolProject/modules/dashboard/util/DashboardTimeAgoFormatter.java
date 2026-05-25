package com.infiniteVision.schoolProject.modules.dashboard.util;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Formats relative timestamps for the dashboard activity feed.
 */
public final class DashboardTimeAgoFormatter {

    private DashboardTimeAgoFormatter() {}

    /**
     * Returns a human-readable relative time (e.g. {@code 5 minutes ago}).
     */
    public static String format(LocalDateTime createdAt) {
        if (createdAt == null) {
            return "";
        }
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.isNegative()) {
            return "just now";
        }
        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return "just now";
        }
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        long days = hours / 24;
        if (days < 30) {
            return days + (days == 1 ? " day ago" : " days ago");
        }
        long months = days / 30;
        if (months < 12) {
            return months + (months == 1 ? " month ago" : " months ago");
        }
        long years = months / 12;
        return years + (years == 1 ? " year ago" : " years ago");
    }
}
