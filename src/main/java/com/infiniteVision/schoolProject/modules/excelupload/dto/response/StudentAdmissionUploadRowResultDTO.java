package com.infiniteVision.schoolProject.modules.excelupload.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Per-row outcome for student admission Excel upload.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAdmissionUploadRowResultDTO {

    private int rowNumber;
    private String admissionNo;
    private boolean success;
    private boolean dryRun;
    private Long studentId;
    private String message;
    private List<String> errors;
}
