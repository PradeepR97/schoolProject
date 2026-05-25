package com.infiniteVision.schoolProject.modules.excelupload.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Summary of student admission Excel bulk upload.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkStudentAdmissionUploadResponseDTO {

    private int requestedCount;
    private int successCount;
    private int failedCount;
    private boolean dryRun;
    private List<StudentAdmissionUploadRowResultDTO> results;
}
