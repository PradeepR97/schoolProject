package com.infiniteVision.schoolProject.modules.dashboard.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Full payload for GET /api/v1/dashboard/summary.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponseDTO {

    private DashboardStatsResponseDTO dashboardStats;
    private List<RecentPaymentResponseDTO> recentPayments;
    private List<ActivityFeedResponseDTO> activityFeed;
    private List<RecentAdmissionResponseDTO> recentAdmissions;
    private List<PendingScholarshipResponseDTO> pendingScholarships;
}
