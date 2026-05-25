package com.infiniteVision.schoolProject.modules.reports.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.reports.enums.ReportType;
import com.infiniteVision.schoolProject.modules.reports.model.ReportMonthYearRange;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validates report filter parameters before export.
 */
@Component
@RequiredArgsConstructor
public class ReportFilterValidator {

    private static final int MIN_CALENDAR_MONTH = 1;
    private static final int MAX_CALENDAR_MONTH = 12;
    private static final int MIN_CALENDAR_YEAR = 2000;
    private static final int MAX_CALENDAR_YEAR = 2100;

    private static final Set<ReportType> MONTH_YEAR_RANGE_REPORT_TYPES =
            EnumSet.of(ReportType.FEE_COLLECTION, ReportType.PENDING_FEES, ReportType.SCHOLARSHIP);

    private final AcademicYearRepository academicYearRepository;
    private final ClassMasterRepository classMasterRepository;

    public void validate(
            Long academicYearId,
            Long classId,
            Integer startMonth,
            Integer startYear,
            Integer endMonth,
            Integer endYear,
            ReportType reportType) {
        List<String> errors = new ArrayList<>();
        if (academicYearId == null) {
            errors.add(MessageConstants.REPORT_ACADEMIC_YEAR_REQUIRED);
        } else if (academicYearRepository.findByIdAndDeletedFalse(academicYearId).isEmpty()) {
            throw new ResourceNotFoundException(MessageConstants.ACADEMIC_YEAR_NOT_FOUND);
        }
        if (classId != null) {
            var classMaster = classMasterRepository
                    .findByIdAndDeletedFalse(classId)
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.CLASS_NOT_FOUND));
            if (academicYearId != null
                    && classMaster.getAcademicYear() != null
                    && !classMaster.getAcademicYear().getId().equals(academicYearId)) {
                throw new ValidationException(MessageConstants.CLASS_ACADEMIC_YEAR_MISMATCH);
            }
        }
        if (requiresMonthYearRange(reportType)) {
            validateMonthYearRangeRequired(startMonth, startYear, endMonth, endYear, errors);
        } else if (hasAnyMonthYearParam(startMonth, startYear, endMonth, endYear)) {
            errors.add(MessageConstants.REPORT_MONTH_YEAR_RANGE_NOT_SUPPORTED_FOR_STUDENT);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    public static boolean requiresMonthYearRange(ReportType reportType) {
        return MONTH_YEAR_RANGE_REPORT_TYPES.contains(reportType);
    }

    /**
     * Builds inclusive range; throws {@link ValidationException} when invalid.
     */
    public ReportMonthYearRange resolveMonthYearRange(
            Integer startMonth, Integer startYear, Integer endMonth, Integer endYear) {
        YearMonth rangeStart = toYearMonth(startMonth, startYear, "start");
        YearMonth rangeEnd = toYearMonth(endMonth, endYear, "end");
        if (rangeStart == null || rangeEnd == null) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED);
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException(MessageConstants.REPORT_END_BEFORE_START_MONTH_YEAR);
        }
        return new ReportMonthYearRange(rangeStart, rangeEnd);
    }

    private static void validateMonthYearRangeRequired(
            Integer startMonth,
            Integer startYear,
            Integer endMonth,
            Integer endYear,
            List<String> errors) {
        if (startMonth == null) {
            errors.add(MessageConstants.REPORT_START_MONTH_REQUIRED);
        }
        if (startYear == null) {
            errors.add(MessageConstants.REPORT_START_YEAR_REQUIRED);
        }
        if (endMonth == null) {
            errors.add(MessageConstants.REPORT_END_MONTH_REQUIRED);
        }
        if (endYear == null) {
            errors.add(MessageConstants.REPORT_END_YEAR_REQUIRED);
        }
    }

    private static boolean hasAnyMonthYearParam(
            Integer startMonth, Integer startYear, Integer endMonth, Integer endYear) {
        return startMonth != null || startYear != null || endMonth != null || endYear != null;
    }

    private static YearMonth toYearMonth(Integer month, Integer year, String label) {
        if (month == null || year == null) {
            return null;
        }
        if (month < MIN_CALENDAR_MONTH || month > MAX_CALENDAR_MONTH) {
            throw new ValidationException(MessageConstants.REPORT_INVALID_MONTH_VALUE);
        }
        if (year < MIN_CALENDAR_YEAR || year > MAX_CALENDAR_YEAR) {
            throw new ValidationException(MessageConstants.REPORT_INVALID_YEAR_VALUE);
        }
        try {
            return YearMonth.of(year, month);
        } catch (RuntimeException ex) {
            throw new ValidationException(MessageConstants.REPORT_INVALID_MONTH_YEAR + " (" + label + ")");
        }
    }
}
