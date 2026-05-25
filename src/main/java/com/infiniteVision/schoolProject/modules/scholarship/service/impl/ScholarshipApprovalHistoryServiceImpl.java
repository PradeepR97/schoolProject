package com.infiniteVision.schoolProject.modules.scholarship.service.impl;

import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipApprovalHistoryResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.entity.ScholarshipApprovalHistory;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApprovalAction;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import com.infiniteVision.schoolProject.modules.scholarship.repository.ScholarshipApprovalHistoryRepository;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipApprovalHistoryService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Append-only scholarship approval history persistence.
 */
@Service
@RequiredArgsConstructor
public class ScholarshipApprovalHistoryServiceImpl implements ScholarshipApprovalHistoryService {

    private final ScholarshipApprovalHistoryRepository historyRepository;

    @Override
    @Transactional
    public void record(
            Long applicationId,
            ScholarshipApprovalAction action,
            String performedBy,
            String remarks,
            ScholarshipApplicationStatus previousStatus,
            ScholarshipApplicationStatus newStatus) {
        ScholarshipApprovalHistory row = ScholarshipApprovalHistory.builder()
                .applicationId(applicationId)
                .action(action)
                .performedBy(performedBy)
                .performedAt(LocalDateTime.now())
                .remarks(remarks)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .build();
        historyRepository.save(row);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScholarshipApprovalHistoryResponseDTO> listByApplicationId(Long applicationId) {
        return historyRepository.findAllByApplicationIdOrderByPerformedAtDesc(applicationId).stream()
                .map(this::toResponse)
                .toList();
    }

    private ScholarshipApprovalHistoryResponseDTO toResponse(ScholarshipApprovalHistory row) {
        return ScholarshipApprovalHistoryResponseDTO.builder()
                .historyId(row.getHistoryId())
                .applicationId(row.getApplicationId())
                .action(row.getAction())
                .performedBy(row.getPerformedBy())
                .performedAt(row.getPerformedAt())
                .remarks(row.getRemarks())
                .previousStatus(row.getPreviousStatus())
                .newStatus(row.getNewStatus())
                .build();
    }
}
