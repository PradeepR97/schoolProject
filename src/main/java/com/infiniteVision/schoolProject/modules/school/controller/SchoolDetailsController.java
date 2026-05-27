package com.infiniteVision.schoolProject.modules.school.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.school.constants.SchoolApiConstants;
import com.infiniteVision.schoolProject.modules.school.dto.request.CreateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.dto.request.UpdateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.dto.response.SchoolDetailsResponseDTO;
import com.infiniteVision.schoolProject.modules.school.service.SchoolDetailsService;
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
 * REST APIs for school profile configuration.
 */
@RestController
@RequestMapping(value = SchoolApiConstants.SCHOOL_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SchoolDetailsController {

    private final SchoolDetailsService schoolDetailsService;

    /** GET /api/v1/schools */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PagedResponseDTO<SchoolDetailsResponseDTO>>> listSchools(
            @RequestParam(defaultValue = "false") boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponseDTO<SchoolDetailsResponseDTO> data = schoolDetailsService.listSchools(activeOnly, page, size);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SCHOOLS_LISTED_SUCCESS, data));
    }

    /** GET /api/v1/schools/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<SchoolDetailsResponseDTO>> getSchoolById(@PathVariable Long id) {
        SchoolDetailsResponseDTO data = schoolDetailsService.getSchoolById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SCHOOL_RETRIEVED_SUCCESS, data));
    }

    /** POST /api/v1/schools */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<SchoolDetailsResponseDTO>> createSchool(
            @Valid @RequestBody CreateSchoolDetailsRequestDTO request) {
        SchoolDetailsResponseDTO data = schoolDetailsService.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.SCHOOL_CREATED_SUCCESS, data));
    }

    /** PUT /api/v1/schools/{id} */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<SchoolDetailsResponseDTO>> updateSchool(
            @PathVariable Long id, @Valid @RequestBody UpdateSchoolDetailsRequestDTO request) {
        SchoolDetailsResponseDTO data = schoolDetailsService.updateSchool(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SCHOOL_UPDATED_SUCCESS, data));
    }

    /** DELETE /api/v1/schools/{id} */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable Long id) {
        schoolDetailsService.deleteSchool(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SCHOOL_DELETED_SUCCESS));
    }
}
