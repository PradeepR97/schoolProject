package com.infiniteVision.schoolProject.modules.excelupload.config;

import com.infiniteVision.schoolProject.modules.excelupload.template.StudentAdmissionExcelTemplateService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Writes the two-row sample Excel file to disk on startup when enabled.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelUploadSampleFileInitializer implements ApplicationRunner {

    private final StudentAdmissionExcelTemplateService templateService;

    @Value("${excel-upload.student-admission.write-sample-on-startup:true}")
    private boolean writeSampleOnStartup;

    @Value("${excel-upload.student-admission.sample-output-dir:samples}")
    private String sampleOutputDir;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!writeSampleOnStartup) {
            return;
        }
        Path outputDirectory = Paths.get(sampleOutputDir).toAbsolutePath().normalize();
        Files.createDirectories(outputDirectory);
        Path outputFile = outputDirectory.resolve(StudentAdmissionExcelTemplateService.SAMPLE_FILENAME);
        byte[] sampleBytes = templateService.buildSampleBytes();
        Files.write(outputFile, sampleBytes);
        log.info("Student admission sample Excel written to {}", outputFile);
    }
}
