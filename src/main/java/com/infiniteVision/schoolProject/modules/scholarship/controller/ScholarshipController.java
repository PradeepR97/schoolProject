package com.infiniteVision.schoolProject.modules.scholarship.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.scholarship.constants.ScholarshipApiConstants;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.CreateScholarshipRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.UpdateScholarshipRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for scholarship scheme management.
 */
@RestController
@RequestMapping(value = ScholarshipApiConstants.SCHOLARSHIP_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ScholarshipController {

    private final ScholarshipService scholarshipService;

    /** POST /api/v2/scholarships — create scholarship scheme. */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<ScholarshipResponseDTO>> createScholarship(
            @Valid @RequestBody CreateScholarshipRequestDTO request) {
        ScholarshipResponseDTO created = scholarshipService.createScholarship(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.SCHOLARSHIP_CREATED_SUCCESS, created));
    }

    /** GET /api/v2/scholarships — list active scholarship schemes. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<ScholarshipResponseDTO>>> listActiveScholarships() {
        List<ScholarshipResponseDTO> scholarships = scholarshipService.listActiveScholarships();
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SCHOLARSHIPS_LISTED_SUCCESS, scholarships));
    }

    /** GET /api/v2/scholarships/{schemeId} — get active scholarship by id. */
    @GetMapping("/{schemeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<ScholarshipResponseDTO>> getScholarshipById(@PathVariable Long schemeId) {
        ScholarshipResponseDTO scholarship = scholarshipService.getScholarshipById(schemeId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SCHOLARSHIP_RETRIEVED_SUCCESS, scholarship));
    }

    /** PUT /api/v2/scholarships/{schemeId} — update scholarship scheme. */
    @PutMapping("/{schemeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<ScholarshipResponseDTO>> updateScholarship(
            @PathVariable Long schemeId, @Valid @RequestBody UpdateScholarshipRequestDTO request) {
        ScholarshipResponseDTO updated = scholarshipService.updateScholarship(schemeId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SCHOLARSHIP_UPDATED_SUCCESS, updated));
    }

    /**
     * DELETE /api/v2/scholarships/{schemeId} — deactivates scheme ({@code is_active = false}) and records
     * {@code deleted_at} / {@code deleted_by} for the authenticated user.
     */
    @DeleteMapping("/{schemeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> deactivateScholarship(@PathVariable Long schemeId) {
        scholarshipService.deactivateScholarship(schemeId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SCHOLARSHIP_DELETED_SUCCESS));
    }
}
