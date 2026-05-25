package com.infiniteVision.schoolProject.modules.academic.constants;

import com.infiniteVision.schoolProject.constants.ApiConstants;

/**
 * Academic module API paths (reserved for future REST endpoints).
 */
public final class AcademicApiConstants {

    private AcademicApiConstants() {
    }

    public static final String ACADEMIC_BASE = ApiConstants.API_V1_PREFIX + "/academic";
    public static final String YEARS = ACADEMIC_BASE + "/years";
    public static final String CLASSES = ACADEMIC_BASE + "/classes";
    public static final String SECTIONS = ACADEMIC_BASE + "/sections";
}
