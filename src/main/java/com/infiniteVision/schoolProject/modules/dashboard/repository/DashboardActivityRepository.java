package com.infiniteVision.schoolProject.modules.dashboard.repository;

import com.infiniteVision.schoolProject.modules.dashboard.entity.DashboardActivity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence for {@link DashboardActivity} feed rows.
 */
@Repository
public interface DashboardActivityRepository extends JpaRepository<DashboardActivity, Long> {

    List<DashboardActivity> findTop5ByOrderByCreatedAtDesc();
}
