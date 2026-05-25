package com.infiniteVision.schoolProject.modules.reports.model;

import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Inclusive calendar range from start month/year through end month/year.
 */
@Getter
@AllArgsConstructor
public class ReportMonthYearRange {

    private final YearMonth start;
    private final YearMonth end;
}
