package com.infiniteVision.schoolProject.modules.dashboard.service;

import com.infiniteVision.schoolProject.modules.dashboard.enums.DashboardActivityType;

/**
 * Publishes rows to {@code dashboard_activities} when domain events occur.
 */
public interface DashboardActivityPublisher {

    void publish(
            DashboardActivityType activityType,
            String title,
            String description,
            Long referenceId,
            String createdBy);
}
