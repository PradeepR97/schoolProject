package com.infiniteVision.schoolProject.modules.masterdata.service;

import com.infiniteVision.schoolProject.modules.masterdata.dto.MasterDataOptionDTO;
import java.util.List;
import java.util.Map;

/**
 * Aggregates enum and master-table options for UI dropdowns.
 */
public interface MasterDataService {

    /**
     * Returns all master data lists. When {@code academicYearId} is set, {@code class} is filtered to that year.
     */
    Map<String, List<MasterDataOptionDTO>> getMasterData(Long academicYearId);
}
