package com.infiniteVision.schoolProject.modules.dashboard.service.impl;

import com.infiniteVision.schoolProject.modules.dashboard.constants.DashboardConstants;
import com.infiniteVision.schoolProject.modules.dashboard.dto.response.DashboardStatsResponseDTO;
import com.infiniteVision.schoolProject.modules.dashboard.dto.response.DashboardSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.dashboard.mapper.DashboardMapper;
import com.infiniteVision.schoolProject.modules.dashboard.repository.DashboardActivityRepository;
import com.infiniteVision.schoolProject.modules.dashboard.repository.DashboardSummaryRepository;
import com.infiniteVision.schoolProject.modules.dashboard.service.DashboardService;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds the admin dashboard summary using optimized projection queries.
 * <p>
 * Redis caching can be added later with {@link DashboardConstants#CACHE_SUMMARY} and a short TTL.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final List<LedgerStatus> PENDING_LEDGER_STATUSES =
            List.of(LedgerStatus.PENDING, LedgerStatus.PARTIAL, LedgerStatus.OVERDUE);

    private static final List<ScholarshipApplicationStatus> PENDING_SCHOLARSHIP_STATUSES = List.of(
            ScholarshipApplicationStatus.PENDING,
            ScholarshipApplicationStatus.PRINCIPAL_APPROVED);

    private final DashboardSummaryRepository dashboardSummaryRepository;
    private final DashboardActivityRepository dashboardActivityRepository;
    private final DashboardMapper dashboardMapper;

    /**
     * Loads stats, five recent payments, five activities, five admissions, and five pending scholarships.
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponseDTO getSummary(Long academicYearId) {
        log.info("Dashboard summary requested academicYearId={}", academicYearId);

        DashboardStatsResponseDTO stats = loadStats(academicYearId);
        var pageable = PageRequest.of(0, DashboardConstants.RECENT_ITEMS_LIMIT);

        return DashboardSummaryResponseDTO.builder()
                .dashboardStats(stats)
                .recentPayments(dashboardMapper.toRecentPayments(
                        dashboardSummaryRepository.findRecentPayments(pageable)))
                .activityFeed(dashboardMapper.toActivityFeed(
                        dashboardActivityRepository.findTop5ByOrderByCreatedAtDesc()))
                .recentAdmissions(dashboardMapper.toRecentAdmissions(
                        dashboardSummaryRepository.findRecentAdmissions(academicYearId, pageable)))
                .pendingScholarships(dashboardMapper.toPendingScholarships(
                        dashboardSummaryRepository.findPendingScholarships(
                                academicYearId, PENDING_SCHOLARSHIP_STATUSES, pageable)))
                .build();
    }

    private DashboardStatsResponseDTO loadStats(Long academicYearId) {
        long totalStudents = dashboardSummaryRepository.countActiveStudents(academicYearId);
        BigDecimal feeCollected = dashboardSummaryRepository.sumFeeCollected(
                academicYearId, PaymentRecordStatus.SUCCESS);
        BigDecimal pendingFees = dashboardSummaryRepository.sumPendingFees(
                academicYearId, PENDING_LEDGER_STATUSES);
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long newAdmissions = dashboardSummaryRepository.countNewAdmissionsSince(academicYearId, monthStart);
        return dashboardMapper.toStats(totalStudents, feeCollected, pendingFees, newAdmissions);
    }
}
