package com.infiniteVision.schoolProject.modules.lookup.constants;

import com.infiniteVision.schoolProject.modules.lookup.dto.LookupOptionDTO;
import java.util.List;

/**
 * Static dropdown options not stored as Java enums (fee categories).
 */
public final class LookupStaticOptions {

    private LookupStaticOptions() {
    }

    public static final List<LookupOptionDTO> FEE_CATEGORIES = List.of(
            option(1L, "Academic", "ACADEMIC"),
            option(2L, "Transport", "TRANSPORT"),
            option(3L, "Hostel", "HOSTEL"),
            option(4L, "Optional", "OPTIONAL"));

    private static LookupOptionDTO option(long id, String label, String value) {
        return LookupOptionDTO.builder().id(id).label(label).value(value).build();
    }
}
