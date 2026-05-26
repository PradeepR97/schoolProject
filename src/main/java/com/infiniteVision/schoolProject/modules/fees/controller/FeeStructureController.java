package com.infiniteVision.schoolProject.modules.fees.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.fees.constants.FeesApiConstants;
import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeStructureMatrixResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeStructureResponseDTO;
import java.util.List;
import com.infiniteVision.schoolProject.modules.fees.service.FeeStructureService;
import jakarta.validation.Valid;
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
 * REST APIs for fee structure (amount per class, year, fee head, term).
 */
@RestController
@RequestMapping(value = FeesApiConstants.FEE_STRUCTURE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FeeStructureController {

    private final FeeStructureService feeStructureService;

    /**
     * GET /api/v1/fees/structure — paginated list with optional filters.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<PagedResponseDTO<FeeStructureResponseDTO>>> listFeeStructures(
            @RequestParam(required = false) Long academicYearId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) Long feeHeadId,
            @RequestParam(defaultValue = "false") boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponseDTO<FeeStructureResponseDTO> data = feeStructureService.listFeeStructures(
                academicYearId, classId, sectionId, feeHeadId, activeOnly, page, size);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_STRUCTURES_LISTED_SUCCESS, data));
    }

    /**
     * GET /api/v1/fees/structure/matrix — class-wise fee matrix for an academic year.
     */
    @GetMapping("/matrix")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<FeeStructureMatrixResponseDTO>> getFeeStructureMatrix(
            @RequestParam Long academicYearId,
            @RequestParam Long classId,
            @RequestParam Long sectionId) {
        FeeStructureMatrixResponseDTO data =
                feeStructureService.getFeeStructureMatrix(academicYearId, classId, sectionId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_STRUCTURE_MATRIX_RETRIEVED_SUCCESS, data));
    }

    /**
     * GET /api/v1/fees/structure/{id} — single fee structure detail.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<FeeStructureResponseDTO>> getFeeStructureById(@PathVariable Long id) {
        FeeStructureResponseDTO data = feeStructureService.getFeeStructureById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_STRUCTURE_RETRIEVED_SUCCESS, data));
    }

    /**
     * POST /api/v1/fees/structure — create fee structure.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<FeeStructureResponseDTO>> createFeeStructure(
            @Valid @RequestBody CreateFeeStructureRequestDTO request) {
        FeeStructureResponseDTO data = feeStructureService.createFeeStructure(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.FEE_STRUCTURE_CREATED_SUCCESS, data));
    }

    /**
     * POST /api/v1/fees/structure/bulk — import multiple rows from fee document data.
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<FeeStructureResponseDTO>>> bulkCreateFeeStructures(
            @Valid @RequestBody BulkCreateFeeStructureRequestDTO request) {
        List<FeeStructureResponseDTO> data = feeStructureService.bulkCreateFeeStructures(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.FEE_STRUCTURE_BULK_CREATED_SUCCESS, data));
    }

    /**
     * PUT /api/v1/fees/structure/{id} — partial update.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<FeeStructureResponseDTO>> updateFeeStructure(
            @PathVariable Long id, @Valid @RequestBody UpdateFeeStructureRequestDTO request) {
        FeeStructureResponseDTO data = feeStructureService.updateFeeStructure(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_STRUCTURE_UPDATED_SUCCESS, data));
    }

    /**
     * DELETE /api/v1/fees/structure/{id} — soft delete.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> deleteFeeStructure(@PathVariable Long id) {
        feeStructureService.deleteFeeStructure(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_STRUCTURE_DELETED_SUCCESS));
    }
}
