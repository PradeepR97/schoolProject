package com.infiniteVision.schoolProject.modules.fees.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.fees.constants.FeesApiConstants;
import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeTypeResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.service.FeeTypeService;
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
 * REST APIs for fee type master data (tuition, transport, exam, etc.).
 */
@RestController
@RequestMapping(value = FeesApiConstants.FEE_TYPES, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FeeTypeController {

    private final FeeTypeService feeTypeService;

    /** GET /api/v1/fees/types */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<FeeTypeResponseDTO>>> listFeeTypes(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<FeeTypeResponseDTO> data = feeTypeService.listFeeTypes(activeOnly);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_TYPES_LISTED_SUCCESS, data));
    }

    /** GET /api/v1/fees/types/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<FeeTypeResponseDTO>> getFeeTypeById(@PathVariable Long id) {
        FeeTypeResponseDTO data = feeTypeService.getFeeTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_TYPE_RETRIEVED_SUCCESS, data));
    }

    /** POST /api/v1/fees/types */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<FeeTypeResponseDTO>> createFeeType(
            @Valid @RequestBody CreateFeeTypeRequestDTO request) {
        FeeTypeResponseDTO data = feeTypeService.createFeeType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.FEE_TYPE_CREATED_SUCCESS, data));
    }

    /** POST /api/v1/fees/types/bulk */
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<FeeTypeResponseDTO>>> bulkCreateFeeTypes(
            @Valid @RequestBody BulkCreateFeeTypeRequestDTO request) {
        List<FeeTypeResponseDTO> data = feeTypeService.bulkCreateFeeTypes(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.FEE_TYPES_BULK_CREATED_SUCCESS, data));
    }

    /** PUT /api/v1/fees/types/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<FeeTypeResponseDTO>> updateFeeType(
            @PathVariable Long id, @Valid @RequestBody UpdateFeeTypeRequestDTO request) {
        FeeTypeResponseDTO data = feeTypeService.updateFeeType(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_TYPE_UPDATED_SUCCESS, data));
    }

    /** DELETE /api/v1/fees/types/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> deleteFeeType(@PathVariable Long id) {
        feeTypeService.deleteFeeType(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.FEE_TYPE_DELETED_SUCCESS));
    }
}
