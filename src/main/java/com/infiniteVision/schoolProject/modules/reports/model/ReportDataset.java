package com.infiniteVision.schoolProject.modules.reports.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Tabular report content for Excel or PDF export.
 */
@Getter
@Builder
@AllArgsConstructor
public class ReportDataset {

    private final String sheetName;
    private final String reportTitle;
    private final List<String> headers;
    private final List<List<String>> rows;
}
