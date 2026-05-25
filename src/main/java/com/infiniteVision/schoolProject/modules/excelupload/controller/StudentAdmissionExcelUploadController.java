package com.infiniteVision.schoolProject.modules.excelupload.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.http.FileAttachmentHeaders;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.excelupload.constants.ExcelUploadApiConstants;
import com.infiniteVision.schoolProject.modules.excelupload.dto.response.BulkStudentAdmissionUploadResponseDTO;
import com.infiniteVision.schoolProject.modules.excelupload.service.StudentAdmissionExcelUploadService;
import com.infiniteVision.schoolProject.modules.excelupload.template.StudentAdmissionExcelTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Excel bulk upload for student admissions.
 */
@RestController
@RequestMapping(value = ExcelUploadApiConstants.EXCEL_UPLOAD_BASE)
@RequiredArgsConstructor
public class StudentAdmissionExcelUploadController {

    private final StudentAdmissionExcelUploadService studentAdmissionExcelUploadService;

    /** GET /api/v1/excel-upload/student-admissions/template — empty headers only. */
    @GetMapping("/student-admissions/template")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] body = studentAdmissionExcelUploadService.buildTemplate();
        return excelAttachment(body, StudentAdmissionExcelTemplateService.TEMPLATE_FILENAME);
    }

    /** GET /api/v1/excel-upload/student-admissions/sample — template with 2 sample students. */
    @GetMapping("/student-admissions/sample")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<byte[]> downloadSample() {
        byte[] body = studentAdmissionExcelUploadService.buildSample();
        return excelAttachment(body, StudentAdmissionExcelTemplateService.SAMPLE_FILENAME);
    }

    private static ResponseEntity<byte[]> excelAttachment(byte[] body, String filename) {
        return FileAttachmentHeaders.okAttachment(
                body,
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                filename);
    }

    /** POST /api/v1/excel-upload/student-admissions/upload */
    @PostMapping(value = "/student-admissions/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<BulkStudentAdmissionUploadResponseDTO>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean dryRun) {
        BulkStudentAdmissionUploadResponseDTO data =
                studentAdmissionExcelUploadService.upload(file, dryRun);
        String message = dryRun
                ? MessageConstants.EXCEL_UPLOAD_DRY_RUN_SUCCESS
                : MessageConstants.EXCEL_UPLOAD_BULK_ADMISSION_COMPLETED;
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }
}
