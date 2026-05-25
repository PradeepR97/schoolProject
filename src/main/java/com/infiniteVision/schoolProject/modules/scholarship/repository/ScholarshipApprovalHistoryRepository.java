package com.infiniteVision.schoolProject.modules.scholarship.repository;

import com.infiniteVision.schoolProject.modules.scholarship.entity.ScholarshipApprovalHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Scholarship approval timeline rows.
 */
@Repository
public interface ScholarshipApprovalHistoryRepository
        extends JpaRepository<ScholarshipApprovalHistory, Long> {

    List<ScholarshipApprovalHistory> findAllByApplicationIdOrderByPerformedAtDesc(Long applicationId);
}
