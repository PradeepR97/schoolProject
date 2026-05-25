package com.infiniteVision.schoolProject.modules.excelupload.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Registers Excel upload configuration properties.
 */
@Configuration
@EnableConfigurationProperties(ExcelUploadProperties.class)
public class ExcelUploadModuleConfig {
}
