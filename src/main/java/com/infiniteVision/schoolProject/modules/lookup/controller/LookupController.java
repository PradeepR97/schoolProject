package com.infiniteVision.schoolProject.modules.lookup.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.lookup.constants.LookupApiConstants;
import com.infiniteVision.schoolProject.modules.lookup.dto.LookupOptionDTO;
import com.infiniteVision.schoolProject.modules.lookup.service.LookupService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dropdown / lookup options for forms (enums + master tables).
 */
@RestController
@RequestMapping(value = LookupApiConstants.LOOKUPS_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LookupController {

    private final LookupService lookupService;

    /**
     * GET /api/v1/lookups — all dropdown options as {@code { id, label, value }} lists.
     *
     * @param academicYearId optional; when set, {@code class} list is limited to that academic year
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<LookupOptionDTO>>>> getLookups(
            @RequestParam(required = false) Long academicYearId) {
        Map<String, List<LookupOptionDTO>> data = lookupService.getAllLookups(academicYearId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.LOOKUPS_RETRIEVED_SUCCESS, data));
    }
}
