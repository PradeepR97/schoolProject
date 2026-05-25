package com.infiniteVision.schoolProject.modules.reports.service;

import com.infiniteVision.schoolProject.modules.reports.dto.response.ReportTypeResponseDTO;
import com.infiniteVision.schoolProject.modules.reports.enums.ReportExportFormat;
import com.infiniteVision.schoolProject.modules.reports.enums.ReportType;
import com.infiniteVision.schoolProject.modules.reports.model.ReportExportResult;
import java.util.List;

/**
 * Report catalog and export operations.
 */
public interface ReportsService {

    List<ReportTypeResponseDTO> listReportTypes();

    ReportExportResult exportReport(
            ReportType reportType,
            ReportExportFormat format,
            Long academicYearId,
            Long classId,
            Integer startMonth,
            Integer startYear,
            Integer endMonth,
            Integer endYear);
}
