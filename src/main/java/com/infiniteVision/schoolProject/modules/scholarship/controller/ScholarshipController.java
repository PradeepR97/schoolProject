package com.infiniteVision.schoolProject.modules.scholarship.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.scholarship.constants.ScholarshipApiConstants;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.CreateScholarshipRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.UpdateScholarshipRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.MeritBandResolveResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.MeritBandResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.service.MeritScholarshipBandService;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for scholarship scheme management.
 */
@RestController
@RequestMapping(value = ScholarshipApiConstants.SCHOLARSHIP_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ScholarshipController {

    private final ScholarshipService scholarshipService;
    private final MeritScholarshipBandService meritScholarshipBandService;

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

    /** GET /api/v1/scholarships/merit-bands — mark-based tuition waiver bands for an academic year. */
    @GetMapping("/merit-bands")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<MeritBandResponseDTO>>> listMeritBands(
            @RequestParam Long academicYearId) {
        List<MeritBandResponseDTO> bands = meritScholarshipBandService.listBands(academicYearId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.MERIT_BANDS_LISTED_SUCCESS, bands));
    }

    /** GET /api/v1/scholarships/merit-bands/resolve — resolve discount % from marks. */
    @GetMapping("/merit-bands/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<MeritBandResolveResponseDTO>> resolveMeritBand(
            @RequestParam Long academicYearId, @RequestParam BigDecimal marks) {
        MeritBandResolveResponseDTO resolved =
                meritScholarshipBandService.resolveDiscountPercent(academicYearId, marks);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.MERIT_BAND_RESOLVED_SUCCESS, resolved));
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
