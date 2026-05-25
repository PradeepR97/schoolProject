package com.infiniteVision.schoolProject.modules.scholarship.service;

import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipApprovalHistoryResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApprovalAction;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.util.List;

/**
 * Records and lists scholarship approval workflow history.
 */
public interface ScholarshipApprovalHistoryService {

    void record(
            Long applicationId,
            ScholarshipApprovalAction action,
            String performedBy,
            String remarks,
            ScholarshipApplicationStatus previousStatus,
            ScholarshipApplicationStatus newStatus);

    List<ScholarshipApprovalHistoryResponseDTO> listByApplicationId(Long applicationId);
}
