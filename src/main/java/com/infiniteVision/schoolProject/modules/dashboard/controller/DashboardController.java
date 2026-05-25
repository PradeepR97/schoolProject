package com.infiniteVision.schoolProject.modules.dashboard.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.dashboard.constants.DashboardApiConstants;
import com.infiniteVision.schoolProject.modules.dashboard.dto.response.DashboardSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin dashboard REST APIs.
 */
@RestController
@RequestMapping(value = DashboardApiConstants.DASHBOARD_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/v1/dashboard/summary — stats, recent payments, activity feed, admissions, pending scholarships.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<DashboardSummaryResponseDTO>> getSummary(
            @RequestParam(required = false) Long academicYearId) {
        DashboardSummaryResponseDTO summary = dashboardService.getSummary(academicYearId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.DASHBOARD_SUMMARY_RETRIEVED_SUCCESS, summary));
    }
}
