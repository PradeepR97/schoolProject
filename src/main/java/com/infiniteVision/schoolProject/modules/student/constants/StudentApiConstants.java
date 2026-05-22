package com.infiniteVision.schoolProject.modules.student.constants;

import com.infiniteVision.schoolProject.constants.ApiConstants;

/**
 * Student module API paths.
 */
public final class StudentApiConstants {

    private StudentApiConstants() {
    }

    public static final String STUDENT_BASE = ApiConstants.API_V1_PREFIX + "/students";
    public static final String ADMISSIONS = STUDENT_BASE + "/admissions";
}
