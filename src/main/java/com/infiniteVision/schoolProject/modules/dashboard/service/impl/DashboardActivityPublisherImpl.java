package com.infiniteVision.schoolProject.modules.dashboard.service.impl;

import com.infiniteVision.schoolProject.modules.dashboard.entity.DashboardActivity;
import com.infiniteVision.schoolProject.modules.dashboard.enums.DashboardActivityType;
import com.infiniteVision.schoolProject.modules.dashboard.repository.DashboardActivityRepository;
import com.infiniteVision.schoolProject.modules.dashboard.service.DashboardActivityPublisher;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persists dashboard feed activities; failures are logged and do not roll back the parent transaction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardActivityPublisherImpl implements DashboardActivityPublisher {

    private final DashboardActivityRepository dashboardActivityRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publish(
            DashboardActivityType activityType,
            String title,
            String description,
            Long referenceId,
            String createdBy) {
        try {
            DashboardActivity activity = DashboardActivity.builder()
                    .activityType(activityType)
                    .title(truncate(title, 200))
                    .description(truncate(description, 500))
                    .referenceId(referenceId)
                    .createdBy(truncate(createdBy, 100))
                    .createdAt(LocalDateTime.now())
                    .build();
            dashboardActivityRepository.save(activity);
        } catch (RuntimeException exception) {
            log.error(
                    "Failed to publish dashboard activity type={} referenceId={}",
                    activityType,
                    referenceId,
                    exception);
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }
}
