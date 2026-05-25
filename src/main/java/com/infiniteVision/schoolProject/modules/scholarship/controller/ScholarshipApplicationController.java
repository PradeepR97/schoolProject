package com.infiniteVision.schoolProject.modules.scholarship.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.scholarship.constants.ScholarshipApiConstants;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.BulkScholarshipApprovalRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.BulkScholarshipRejectRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.CreateScholarshipApplicationRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.RejectScholarshipApplicationRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.BulkScholarshipApplicationResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipApplicationResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipApprovalHistoryResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipApplicationService;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipApprovalHistoryService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for per-student scholarship discount applications and single-step approval workflow.
 */
@RestController
@RequestMapping(
        value = ScholarshipApiConstants.SCHOLARSHIP_APPLICATION_BASE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ScholarshipApplicationController {

    private final ScholarshipApplicationService scholarshipApplicationService;
    private final ScholarshipApprovalHistoryService approvalHistoryService;

    /** POST /api/v1/scholarship-applications — submit discount application for a student. */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<ScholarshipApplicationResponseDTO>> createApplication(
            @Valid @RequestBody CreateScholarshipApplicationRequestDTO request) {
        ScholarshipApplicationResponseDTO created = scholarshipApplicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.SCHOLARSHIP_APPLICATION_CREATED_SUCCESS, created));
    }

    /** GET /api/v1/scholarship-applications — paginated list with optional filters. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<PagedResponseDTO<ScholarshipApplicationResponseDTO>>> listApplications(
            @RequestParam(required = false) ScholarshipApplicationStatus status,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponseDTO<ScholarshipApplicationResponseDTO> applications =
                scholarshipApplicationService.listApplications(status, studentId, academicYearId, page, size);
        return ResponseEntity.ok(
                ApiResponse.success(MessageConstants.SCHOLARSHIP_APPLICATIONS_LISTED_SUCCESS, applications));
    }

    /** GET /api/v1/scholarship-applications/{applicationId} */
    @GetMapping("/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<ScholarshipApplicationResponseDTO>> getApplicationById(
            @PathVariable Long applicationId) {
        ScholarshipApplicationResponseDTO application =
                scholarshipApplicationService.getApplicationById(applicationId);
        return ResponseEntity.ok(
                ApiResponse.success(MessageConstants.SCHOLARSHIP_APPLICATION_RETRIEVED_SUCCESS, application));
    }

    /** GET /api/v1/scholarship-applications/students/{studentId} */
    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<PagedResponseDTO<ScholarshipApplicationResponseDTO>>> listByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponseDTO<ScholarshipApplicationResponseDTO> applications =
                scholarshipApplicationService.listByStudent(studentId, page, size);
        return ResponseEntity.ok(
                ApiResponse.success(MessageConstants.SCHOLARSHIP_APPLICATIONS_LISTED_SUCCESS, applications));
    }

    /** GET /api/v1/scholarship-applications/{applicationId}/history */
    @GetMapping("/{applicationId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<List<ScholarshipApprovalHistoryResponseDTO>>> listApprovalHistory(
            @PathVariable Long applicationId) {
        List<ScholarshipApprovalHistoryResponseDTO> history =
                approvalHistoryService.listByApplicationId(applicationId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SCHOLARSHIP_HISTORY_LISTED_SUCCESS, history));
    }

    /** POST /api/v1/scholarship-applications/{applicationId}/approve */
    @PostMapping("/{applicationId}/approve")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<ScholarshipApplicationResponseDTO>> approve(@PathVariable Long applicationId) {
        ScholarshipApplicationResponseDTO approved = scholarshipApplicationService.approve(applicationId);
        return ResponseEntity.ok(
                ApiResponse.success(MessageConstants.SCHOLARSHIP_APPLICATION_APPROVED_SUCCESS, approved));
    }

    /** POST /api/v1/scholarship-applications/{applicationId}/reject */
    @PostMapping("/{applicationId}/reject")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<ScholarshipApplicationResponseDTO>> reject(
            @PathVariable Long applicationId, @Valid @RequestBody RejectScholarshipApplicationRequestDTO request) {
        ScholarshipApplicationResponseDTO rejected = scholarshipApplicationService.reject(applicationId, request);
        return ResponseEntity.ok(
                ApiResponse.success(MessageConstants.SCHOLARSHIP_APPLICATION_REJECTED_SUCCESS, rejected));
    }

    /** POST /api/v1/scholarship-applications/bulk-approve */
    @PostMapping("/bulk-approve")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<BulkScholarshipApplicationResponseDTO>> bulkApprove(
            @Valid @RequestBody BulkScholarshipApprovalRequestDTO request) {
        BulkScholarshipApplicationResponseDTO result = scholarshipApplicationService.bulkApprove(request);
        return ResponseEntity.ok(
                ApiResponse.success(MessageConstants.SCHOLARSHIP_APPLICATION_BULK_APPROVED_SUCCESS, result));
    }

    /** POST /api/v1/scholarship-applications/bulk-reject */
    @PostMapping("/bulk-reject")
    @PreAuthorize("hasAnyRole('PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<BulkScholarshipApplicationResponseDTO>> bulkReject(
            @Valid @RequestBody BulkScholarshipRejectRequestDTO request) {
        BulkScholarshipApplicationResponseDTO result = scholarshipApplicationService.bulkReject(request);
        return ResponseEntity.ok(
                ApiResponse.success(MessageConstants.SCHOLARSHIP_APPLICATION_BULK_REJECTED_SUCCESS, result));
    }
}
