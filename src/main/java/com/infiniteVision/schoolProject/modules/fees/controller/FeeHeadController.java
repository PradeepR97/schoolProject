package com.infiniteVision.schoolProject.modules.fees.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.fees.constants.FeesApiConstants;
import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeHeadResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.service.FeeHeadService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST APIs for fee head master data (multiple payment categories).
 */
@RestController
@RequestMapping(value = FeesApiConstants.FEE_HEADS, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FeeHeadController {

    private final FeeHeadService feeHeadService;

    /** GET /api/v1/fees/heads */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<FeeHeadResponseDTO>>> listFeeHeads(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<FeeHeadResponseDTO> data = feeHeadService.listFeeHeads(activeOnly);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_HEADS_LISTED_SUCCESS, data));
    }

    /** GET /api/v1/fees/heads/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<FeeHeadResponseDTO>> getFeeHeadById(@PathVariable Long id) {
        FeeHeadResponseDTO data = feeHeadService.getFeeHeadById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_HEAD_RETRIEVED_SUCCESS, data));
    }

    /** POST /api/v1/fees/heads */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<FeeHeadResponseDTO>> createFeeHead(
            @Valid @RequestBody CreateFeeHeadRequestDTO request) {
        FeeHeadResponseDTO data = feeHeadService.createFeeHead(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.FEE_HEAD_CREATED_SUCCESS, data));
    }

    /** POST /api/v1/fees/heads/bulk — create multiple fee heads at once */
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<FeeHeadResponseDTO>>> bulkCreateFeeHeads(
            @Valid @RequestBody BulkCreateFeeHeadRequestDTO request) {
        List<FeeHeadResponseDTO> data = feeHeadService.bulkCreateFeeHeads(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.FEE_HEADS_BULK_CREATED_SUCCESS, data));
    }

    /** PUT /api/v1/fees/heads/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<FeeHeadResponseDTO>> updateFeeHead(
            @PathVariable Long id, @Valid @RequestBody UpdateFeeHeadRequestDTO request) {
        FeeHeadResponseDTO data = feeHeadService.updateFeeHead(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_HEAD_UPDATED_SUCCESS, data));
    }

    /** DELETE /api/v1/fees/heads/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> deleteFeeHead(@PathVariable Long id) {
        feeHeadService.deleteFeeHead(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_HEAD_DELETED_SUCCESS));
    }
}
