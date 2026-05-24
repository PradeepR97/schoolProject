package com.infiniteVision.schoolProject.modules.lookup.service;

import com.infiniteVision.schoolProject.modules.lookup.dto.LookupOptionDTO;
import java.util.List;
import java.util.Map;

/**
 * Aggregates enum and master-table options for UI dropdowns.
 */
public interface LookupService {

    /**
     * Returns all lookup lists. When {@code academicYearId} is set, {@code class} is filtered to that year.
     */
    Map<String, List<LookupOptionDTO>> getAllLookups(Long academicYearId);
}
