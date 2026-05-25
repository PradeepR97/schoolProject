package com.infiniteVision.schoolProject.modules.masterdata.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.masterdata.constants.MasterDataApiConstants;
import com.infiniteVision.schoolProject.modules.masterdata.dto.MasterDataOptionDTO;
import com.infiniteVision.schoolProject.modules.masterdata.service.MasterDataService;
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
 * Master data / dropdown options for forms (enums + master tables).
 */
@RestController
@RequestMapping(value = MasterDataApiConstants.MASTER_DATA_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MasterDataController {

    private final MasterDataService masterDataService;

    /**
     * GET /api/v1/master-data — all dropdown options as {@code { id, label, value }} lists.
     *
     * @param academicYearId optional; when set, {@code class} list is limited to that academic year
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<MasterDataOptionDTO>>>> getMasterData(
            @RequestParam(required = false) Long academicYearId) {
        Map<String, List<MasterDataOptionDTO>> data = masterDataService.getMasterData(academicYearId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.MASTER_DATA_RETRIEVED_SUCCESS, data));
    }
}
