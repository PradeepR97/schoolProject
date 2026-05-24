package com.infiniteVision.schoolProject.modules.scholarship.service;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.BulkScholarshipApprovalRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.BulkScholarshipRejectRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.CreateScholarshipApplicationRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.RejectScholarshipApplicationRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.BulkScholarshipApplicationResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipApplicationResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;

/**
 * Student scholarship discount applications with Principal and Correspondent approval.
 */
public interface ScholarshipApplicationService {

    ScholarshipApplicationResponseDTO createApplication(CreateScholarshipApplicationRequestDTO request);

    PagedResponseDTO<ScholarshipApplicationResponseDTO> listApplications(
            ScholarshipApplicationStatus status, Long studentId, Long academicYearId, int page, int size);

    ScholarshipApplicationResponseDTO getApplicationById(Long applicationId);

    PagedResponseDTO<ScholarshipApplicationResponseDTO> listByStudent(Long studentId, int page, int size);

    ScholarshipApplicationResponseDTO approve(Long applicationId);

    ScholarshipApplicationResponseDTO reject(Long applicationId, RejectScholarshipApplicationRequestDTO request);

    BulkScholarshipApplicationResponseDTO bulkApprove(BulkScholarshipApprovalRequestDTO request);

    BulkScholarshipApplicationResponseDTO bulkReject(BulkScholarshipRejectRequestDTO request);
}
