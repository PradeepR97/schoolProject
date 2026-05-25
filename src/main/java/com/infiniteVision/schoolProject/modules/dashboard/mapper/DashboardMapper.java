package com.infiniteVision.schoolProject.modules.dashboard.mapper;

import com.infiniteVision.schoolProject.modules.dashboard.dto.response.ActivityFeedResponseDTO;
import com.infiniteVision.schoolProject.modules.dashboard.dto.response.DashboardStatsResponseDTO;
import com.infiniteVision.schoolProject.modules.dashboard.dto.response.PendingScholarshipResponseDTO;
import com.infiniteVision.schoolProject.modules.dashboard.dto.response.RecentAdmissionResponseDTO;
import com.infiniteVision.schoolProject.modules.dashboard.dto.response.RecentPaymentResponseDTO;
import com.infiniteVision.schoolProject.modules.dashboard.entity.DashboardActivity;
import com.infiniteVision.schoolProject.modules.dashboard.repository.projection.PendingScholarshipProjection;
import com.infiniteVision.schoolProject.modules.dashboard.repository.projection.RecentAdmissionProjection;
import com.infiniteVision.schoolProject.modules.dashboard.repository.projection.RecentPaymentProjection;
import com.infiniteVision.schoolProject.modules.dashboard.util.DashboardTimeAgoFormatter;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Maps dashboard projections and entities to API response DTOs.
 */
@Component
public class DashboardMapper {

    public DashboardStatsResponseDTO toStats(
            long totalStudents, BigDecimal feeCollected, BigDecimal pendingFees, long newAdmissions) {
        return DashboardStatsResponseDTO.builder()
                .totalStudents(totalStudents)
                .feeCollected(feeCollected != null ? feeCollected : BigDecimal.ZERO)
                .pendingFees(pendingFees != null ? pendingFees : BigDecimal.ZERO)
                .newAdmissions(newAdmissions)
                .build();
    }

    public RecentPaymentResponseDTO toRecentPayment(RecentPaymentProjection projection) {
        return RecentPaymentResponseDTO.builder()
                .receiptNo(projection.getReceiptNo())
                .studentName(trimToNull(projection.getStudentName()))
                .className(trimToNull(projection.getClassName()))
                .amount(projection.getAmount())
                .paymentMethod(
                        projection.getPaymentMethod() != null
                                ? projection.getPaymentMethod().name()
                                : null)
                .paymentStatus(
                        projection.getPaymentStatus() != null
                                ? projection.getPaymentStatus().name()
                                : null)
                .build();
    }

    public RecentAdmissionResponseDTO toRecentAdmission(RecentAdmissionProjection projection) {
        return RecentAdmissionResponseDTO.builder()
                .applicationNo(projection.getApplicationNo())
                .studentName(trimToNull(projection.getStudentName()))
                .className(trimToNull(projection.getClassName()))
                .status(projection.getStatus())
                .build();
    }

    public PendingScholarshipResponseDTO toPendingScholarship(PendingScholarshipProjection projection) {
        return PendingScholarshipResponseDTO.builder()
                .studentName(trimToNull(projection.getStudentName()))
                .schemeName(projection.getSchemeName())
                .amount(projection.getAmount())
                .status(projection.getStatus())
                .build();
    }

    public ActivityFeedResponseDTO toActivityFeed(DashboardActivity activity) {
        return ActivityFeedResponseDTO.builder()
                .activityType(activity.getActivityType())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .timeAgo(DashboardTimeAgoFormatter.format(activity.getCreatedAt()))
                .build();
    }

    public List<RecentPaymentResponseDTO> toRecentPayments(List<RecentPaymentProjection> projections) {
        return projections.stream().map(this::toRecentPayment).toList();
    }

    public List<RecentAdmissionResponseDTO> toRecentAdmissions(List<RecentAdmissionProjection> projections) {
        return projections.stream().map(this::toRecentAdmission).toList();
    }

    public List<PendingScholarshipResponseDTO> toPendingScholarships(List<PendingScholarshipProjection> projections) {
        return projections.stream().map(this::toPendingScholarship).toList();
    }

    public List<ActivityFeedResponseDTO> toActivityFeed(List<DashboardActivity> activities) {
        return activities.stream().map(this::toActivityFeed).toList();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
