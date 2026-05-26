package com.infiniteVision.schoolProject.modules.fees.constants;

import com.infiniteVision.schoolProject.constants.ApiConstants;

/**
 * Fees module API paths (reserved for future REST endpoints).
 */
public final class FeesApiConstants {

    private FeesApiConstants() {
    }

    public static final String FEES_BASE = ApiConstants.API_V1_PREFIX + "/fees";
    public static final String FEE_TYPES = FEES_BASE + "/types";
    public static final String FEE_STRUCTURE = FEES_BASE + "/structure";
}
