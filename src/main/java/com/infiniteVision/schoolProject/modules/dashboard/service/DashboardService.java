package com.infiniteVision.schoolProject.modules.dashboard.service;

import com.infiniteVision.schoolProject.modules.dashboard.dto.response.DashboardSummaryResponseDTO;

/**
 * Admin dashboard summary: stats, recent payments, activity feed, admissions, pending scholarships.
 */
public interface DashboardService {

    /**
     * Loads the full dashboard summary for the admin UI.
     *
     * @param academicYearId optional filter for year-scoped metrics; {@code null} means all years
     * @return aggregated dashboard payload
     */
    DashboardSummaryResponseDTO getSummary(Long academicYearId);
}
