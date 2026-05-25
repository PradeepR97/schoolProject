package com.infiniteVision.schoolProject.modules.reports.service.impl;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.reports.dto.response.ReportTypeResponseDTO;
import com.infiniteVision.schoolProject.modules.reports.enums.ReportExportFormat;
import com.infiniteVision.schoolProject.modules.reports.enums.ReportType;
import com.infiniteVision.schoolProject.modules.reports.constants.ReportExportMediaTypes;
import com.infiniteVision.schoolProject.modules.reports.export.ReportExcelExporter;
import com.infiniteVision.schoolProject.modules.reports.export.ReportPdfExporter;
import com.infiniteVision.schoolProject.modules.reports.model.ReportDataset;
import com.infiniteVision.schoolProject.modules.reports.model.ReportExportResult;
import com.infiniteVision.schoolProject.modules.reports.model.ReportMonthYearRange;
import com.infiniteVision.schoolProject.modules.reports.service.ReportContentBuilder;
import com.infiniteVision.schoolProject.modules.reports.service.ReportsService;
import com.infiniteVision.schoolProject.modules.reports.validator.ReportFilterValidator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds report datasets and exports them as Excel or PDF.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportsServiceImpl implements ReportsService {

    private final ReportFilterValidator reportFilterValidator;
    private final ReportContentBuilder reportContentBuilder;
    private final ReportExcelExporter reportExcelExporter;
    private final ReportPdfExporter reportPdfExporter;

    @Override
    public List<ReportTypeResponseDTO> listReportTypes() {
        return Arrays.stream(ReportType.values()).map(this::toReportTypeDto).collect(Collectors.toList());
    }

    @Override
    public ReportExportResult exportReport(
            ReportType reportType,
            ReportExportFormat format,
            Long academicYearId,
            Long classId,
            Integer startMonth,
            Integer startYear,
            Integer endMonth,
            Integer endYear) {
        if (format == null) {
            throw new ValidationException(MessageConstants.REPORT_INVALID_EXPORT_FORMAT);
        }
        reportFilterValidator.validate(
                academicYearId, classId, startMonth, startYear, endMonth, endYear, reportType);
        ReportDataset dataset = buildDataset(
                reportType, academicYearId, classId, startMonth, startYear, endMonth, endYear);
        String baseFilename = reportType.name().toLowerCase(Locale.ROOT) + "-report";
        return switch (format) {
            case EXCEL -> ReportExportResult.builder()
                    .content(reportExcelExporter.export(dataset))
                    .filename(baseFilename + ".xlsx")
                    .mediaType(ReportExportMediaTypes.EXCEL)
                    .build();
            case PDF -> ReportExportResult.builder()
                    .content(reportPdfExporter.export(dataset))
                    .filename(baseFilename + ".pdf")
                    .mediaType(ReportExportMediaTypes.PDF)
                    .build();
        };
    }

    private ReportDataset buildDataset(
            ReportType reportType,
            Long academicYearId,
            Long classId,
            Integer startMonth,
            Integer startYear,
            Integer endMonth,
            Integer endYear) {
        return switch (reportType) {
            case STUDENT -> reportContentBuilder.buildStudentReport(academicYearId, classId);
            case FEE_COLLECTION -> {
                ReportMonthYearRange range =
                        reportFilterValidator.resolveMonthYearRange(startMonth, startYear, endMonth, endYear);
                yield reportContentBuilder.buildFeeCollectionReport(
                        academicYearId, classId, range.getStart(), range.getEnd());
            }
            case PENDING_FEES -> {
                ReportMonthYearRange pendingRange =
                        reportFilterValidator.resolveMonthYearRange(startMonth, startYear, endMonth, endYear);
                yield reportContentBuilder.buildPendingFeesReport(
                        academicYearId, classId, pendingRange.getStart(), pendingRange.getEnd());
            }
            case SCHOLARSHIP -> {
                ReportMonthYearRange scholarshipRange =
                        reportFilterValidator.resolveMonthYearRange(startMonth, startYear, endMonth, endYear);
                yield reportContentBuilder.buildScholarshipReport(
                        academicYearId, classId, scholarshipRange.getStart(), scholarshipRange.getEnd());
            }
        };
    }

    private ReportTypeResponseDTO toReportTypeDto(ReportType reportType) {
        return ReportTypeResponseDTO.builder()
                .reportType(reportType)
                .title(reportTitle(reportType))
                .description(reportDescription(reportType))
                .classFilterSupported(true)
                .monthYearRangeFilterSupported(ReportFilterValidator.requiresMonthYearRange(reportType))
                .build();
    }

    private static String reportTitle(ReportType reportType) {
        return switch (reportType) {
            case STUDENT -> "Student Report";
            case FEE_COLLECTION -> "Fee Collection Report";
            case PENDING_FEES -> "Pending Fees Report";
            case SCHOLARSHIP -> "Scholarship Report";
        };
    }

    private static String reportDescription(ReportType reportType) {
        return switch (reportType) {
            case STUDENT ->
                    "Student roster; optional classId filters one class, omit classId for all classes in the year";
            case FEE_COLLECTION ->
                    "Successful fee payments between start month/year and end month/year (inclusive)";
            case PENDING_FEES ->
                    "Outstanding fee balances (due date in range) with parent contact for follow-up";
            case SCHOLARSHIP -> "Scholarship applications applied between start and end month/year";
        };
    }
}
