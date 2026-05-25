package com.infiniteVision.schoolProject.modules.excelupload.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Limits for student admission Excel bulk upload.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "excel-upload.student-admission")
public class ExcelUploadProperties {

    private int maxRows = 500;
}
