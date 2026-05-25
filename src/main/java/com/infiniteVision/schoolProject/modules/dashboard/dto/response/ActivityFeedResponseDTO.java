package com.infiniteVision.schoolProject.modules.dashboard.dto.response;

import com.infiniteVision.schoolProject.modules.dashboard.enums.DashboardActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Single activity feed item for the dashboard timeline.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFeedResponseDTO {

    private DashboardActivityType activityType;
    private String title;
    private String description;
    private String timeAgo;
}
