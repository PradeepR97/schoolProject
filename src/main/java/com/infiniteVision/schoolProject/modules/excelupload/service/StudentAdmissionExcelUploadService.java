package com.infiniteVision.schoolProject.modules.excelupload.service;

import com.infiniteVision.schoolProject.modules.excelupload.dto.response.BulkStudentAdmissionUploadResponseDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Student admission bulk upload from Excel.
 */
public interface StudentAdmissionExcelUploadService {

    byte[] buildTemplate();

    byte[] buildSample();

    BulkStudentAdmissionUploadResponseDTO upload(MultipartFile file, boolean dryRun);
}
