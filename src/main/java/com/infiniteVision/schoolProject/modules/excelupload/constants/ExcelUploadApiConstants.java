package com.infiniteVision.schoolProject.modules.excelupload.constants;

import com.infiniteVision.schoolProject.constants.ApiConstants;

/**
 * REST paths for Excel bulk upload features.
 */
public final class ExcelUploadApiConstants {

    private ExcelUploadApiConstants() {
    }

    public static final String EXCEL_UPLOAD_BASE = ApiConstants.API_V1_PREFIX + "/excel-upload";
    public static final String STUDENT_ADMISSIONS_TEMPLATE = EXCEL_UPLOAD_BASE + "/upload/student-records-template";
    public static final String STUDENT_ADMISSIONS_UPLOAD = EXCEL_UPLOAD_BASE + "/upload/student-records";
}
