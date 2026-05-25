package com.infiniteVision.schoolProject.modules.reports.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.http.FileAttachmentHeaders;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.reports.constants.ReportExportMediaTypes;
import com.infiniteVision.schoolProject.modules.reports.constants.ReportsApiConstants;
import com.infiniteVision.schoolProject.modules.reports.dto.response.ReportTypeResponseDTO;
import com.infiniteVision.schoolProject.modules.reports.enums.ReportExportFormat;
import com.infiniteVision.schoolProject.modules.reports.enums.ReportType;
import com.infiniteVision.schoolProject.modules.reports.model.ReportExportResult;
import com.infiniteVision.schoolProject.modules.reports.service.ReportsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * School ERP reports (Student, Fee Collection, Pending Fees, Scholarship).
 */
@RestController
@RequestMapping(ReportsApiConstants.REPORTS_BASE)
@RequiredArgsConstructor
public class ReportsController {

    private final ReportsService reportsService;

    /** GET /api/v1/reports — available report types for the UI. */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<List<ReportTypeResponseDTO>>> listReports() {
        return ResponseEntity.ok(
                ApiResponse.success(MessageConstants.REPORTS_LISTED_SUCCESS, reportsService.listReportTypes()));
    }

    /**
     * GET /api/v1/reports/{reportType}/export
     * Fee collection, pending fees, scholarship: startMonth, startYear, endMonth, endYear (1-12, inclusive).
     */
    @GetMapping(
            value = "/{reportType}/export",
            produces = {ReportExportMediaTypes.EXCEL_VALUE, ReportExportMediaTypes.PDF_VALUE})
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<byte[]> exportReport(
            @PathVariable ReportType reportType,
            @RequestParam ReportExportFormat format,
            @RequestParam Long academicYearId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer startMonth,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endMonth,
            @RequestParam(required = false) Integer endYear) {
        ReportExportResult exportResult = reportsService.exportReport(
                reportType, format, academicYearId, classId, startMonth, startYear, endMonth, endYear);
        return FileAttachmentHeaders.okAttachment(
                exportResult.getContent(), exportResult.getMediaType(), exportResult.getFilename());
    }
}
