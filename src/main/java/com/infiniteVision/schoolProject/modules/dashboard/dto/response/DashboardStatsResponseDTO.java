package com.infiniteVision.schoolProject.modules.dashboard.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Aggregate counters for the admin dashboard header cards.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponseDTO {

    private long totalStudents;
    private BigDecimal feeCollected;
    private BigDecimal pendingFees;
    private long newAdmissions;
}
