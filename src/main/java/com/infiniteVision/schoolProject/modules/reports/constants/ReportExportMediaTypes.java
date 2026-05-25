package com.infiniteVision.schoolProject.modules.reports.constants;

import org.springframework.http.MediaType;

/**
 * MIME types for report export responses.
 */
public final class ReportExportMediaTypes {

    public static final String EXCEL_VALUE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final MediaType EXCEL = MediaType.parseMediaType(EXCEL_VALUE);
    public static final String PDF_VALUE = "application/pdf";
    public static final MediaType PDF = MediaType.APPLICATION_PDF;

    private ReportExportMediaTypes() {}
}
