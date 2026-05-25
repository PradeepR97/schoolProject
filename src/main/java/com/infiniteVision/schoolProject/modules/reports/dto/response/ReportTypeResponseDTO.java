package com.infiniteVision.schoolProject.modules.reports.dto.response;

import com.infiniteVision.schoolProject.modules.reports.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Metadata for one available report type.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTypeResponseDTO {

    private ReportType reportType;
    private String title;
    private String description;
    /** True when export supports optional classId (omit for all classes in the academic year). */
    private boolean classFilterSupported;
    /** True when export supports startMonth, startYear, endMonth, endYear query params. */
    private boolean monthYearRangeFilterSupported;
}
