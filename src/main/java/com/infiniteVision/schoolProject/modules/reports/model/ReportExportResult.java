package com.infiniteVision.schoolProject.modules.reports.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.MediaType;

/**
 * Binary export payload with download metadata.
 */
@Getter
@Builder
@AllArgsConstructor
public class ReportExportResult {

    private final byte[] content;
    private final String filename;
    private final MediaType mediaType;
}
