package com.infiniteVision.schoolProject.modules.masterdata.constants;

import com.infiniteVision.schoolProject.modules.masterdata.dto.MasterDataOptionDTO;
import java.util.List;

/**
 * Static dropdown options not stored as Java enums (fee categories).
 */
public final class MasterDataStaticOptions {

    private MasterDataStaticOptions() {
    }

    public static final List<MasterDataOptionDTO> FEE_CATEGORIES = List.of(
            option(1L, "Academic", "ACADEMIC"),
            option(2L, "Transport", "TRANSPORT"),
            option(3L, "Hostel", "HOSTEL"),
            option(4L, "Optional", "OPTIONAL"));

    private static MasterDataOptionDTO option(long id, String label, String value) {
        return MasterDataOptionDTO.builder().id(id).label(label).value(value).build();
    }
}
