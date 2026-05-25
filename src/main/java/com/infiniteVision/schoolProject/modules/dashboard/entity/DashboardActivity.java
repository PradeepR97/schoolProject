package com.infiniteVision.schoolProject.modules.dashboard.entity;

import com.infiniteVision.schoolProject.modules.dashboard.enums.DashboardActivityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Append-only activity row for the admin dashboard feed. Maps to {@code dashboard_activities}.
 */
@Entity
@Table(
        name = "dashboard_activities",
        indexes = {@Index(name = "idx_dashboard_activity_created", columnList = "created_at")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id", updatable = false, nullable = false)
    private Long activityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 50)
    private DashboardActivityType activityType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
