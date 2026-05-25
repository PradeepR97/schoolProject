package com.infiniteVision.schoolProject.modules.excelupload.service.impl;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.excelupload.dto.response.BulkStudentAdmissionUploadResponseDTO;
import com.infiniteVision.schoolProject.modules.excelupload.dto.response.StudentAdmissionUploadRowResultDTO;
import com.infiniteVision.schoolProject.modules.excelupload.parser.ParsedStudentAdmissionRow;
import com.infiniteVision.schoolProject.modules.excelupload.parser.StudentAdmissionExcelParser;
import com.infiniteVision.schoolProject.modules.excelupload.service.StudentAdmissionBulkRowExecutor;
import com.infiniteVision.schoolProject.modules.excelupload.service.StudentAdmissionExcelUploadService;
import com.infiniteVision.schoolProject.modules.excelupload.template.StudentAdmissionExcelTemplateService;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionRequestDTO;
import com.infiniteVision.schoolProject.modules.student.validator.StudentAdmissionValidator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Orchestrates Excel parse, in-file duplicate checks, and per-row admission.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentAdmissionExcelUploadServiceImpl implements StudentAdmissionExcelUploadService {

    private final StudentAdmissionExcelTemplateService templateService;
    private final StudentAdmissionExcelParser excelParser;
    private final StudentAdmissionBulkRowExecutor rowExecutor;
    private final StudentAdmissionValidator studentAdmissionValidator;

    @Override
    public byte[] buildTemplate() {
        return templateService.buildTemplateBytes();
    }

    @Override
    public byte[] buildSample() {
        return templateService.buildSampleBytes();
    }

    @Override
    public BulkStudentAdmissionUploadResponseDTO upload(MultipartFile file, boolean dryRun) {
        validateFile(file);
        List<ParsedStudentAdmissionRow> parsedRows = excelParser.parse(file);
        List<StudentAdmissionUploadRowResultDTO> results = new ArrayList<>();
        Set<String> admissionNumbersInFile = new HashSet<>();
        Set<String> aadharNumbersInFile = new HashSet<>();
        int successCount = 0;
        int failedCount = 0;

        for (ParsedStudentAdmissionRow parsedRow : parsedRows) {
            if (parsedRow.hasErrors()) {
                results.add(parseFailure(parsedRow, dryRun));
                failedCount++;
                continue;
            }
            StudentAdmissionRequestDTO request = parsedRow.getRequest();
            List<String> fileLevelErrors = validateInFileDuplicates(
                    request, parsedRow.getRowNumber(), admissionNumbersInFile, aadharNumbersInFile);
            if (!fileLevelErrors.isEmpty()) {
                results.add(rowFailure(parsedRow.getRowNumber(), request, dryRun, fileLevelErrors));
                failedCount++;
                continue;
            }
            trackInFileKeys(request, admissionNumbersInFile, aadharNumbersInFile);
            StudentAdmissionUploadRowResultDTO result =
                    rowExecutor.processRow(request, parsedRow.getRowNumber(), dryRun);
            results.add(result);
            if (result.isSuccess()) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        log.info(
                "Student admission Excel upload dryRun={} requested={} success={} failed={}",
                dryRun,
                parsedRows.size(),
                successCount,
                failedCount);

        return BulkStudentAdmissionUploadResponseDTO.builder()
                .requestedCount(parsedRows.size())
                .successCount(successCount)
                .failedCount(failedCount)
                .dryRun(dryRun)
                .results(results)
                .build();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException(MessageConstants.EXCEL_UPLOAD_FILE_REQUIRED);
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw new ValidationException(MessageConstants.EXCEL_UPLOAD_INVALID_FILE_TYPE);
        }
    }

    private List<String> validateInFileDuplicates(
            StudentAdmissionRequestDTO request,
            int rowNumber,
            Set<String> admissionNumbersInFile,
            Set<String> aadharNumbersInFile) {
        List<String> errors = new ArrayList<>();
        String admissionNo = request.getStudent().getAdmissionNo().trim();
        if (!admissionNumbersInFile.add(admissionNo)) {
            errors.add(MessageConstants.EXCEL_UPLOAD_DUPLICATE_ADMISSION_NO_IN_FILE + " at row " + rowNumber);
        }
        String studentAadhar = trimToNull(request.getStudent().getAadharNumber());
        if (studentAadhar != null && !aadharNumbersInFile.add(studentAadhar)) {
            errors.add(MessageConstants.EXCEL_UPLOAD_DUPLICATE_AADHAR_IN_FILE + " at row " + rowNumber);
        }
        String documentAadhar = studentAdmissionValidator.resolveDocumentAadhar(
                request.getStudent(), request.getDocuments());
        if (documentAadhar != null
                && !documentAadhar.equals(studentAadhar)
                && !aadharNumbersInFile.add(documentAadhar)) {
            errors.add(MessageConstants.EXCEL_UPLOAD_DUPLICATE_AADHAR_IN_FILE + " at row " + rowNumber);
        }
        return errors;
    }

    private void trackInFileKeys(
            StudentAdmissionRequestDTO request,
            Set<String> admissionNumbersInFile,
            Set<String> aadharNumbersInFile) {
        admissionNumbersInFile.add(request.getStudent().getAdmissionNo().trim());
        String studentAadhar = trimToNull(request.getStudent().getAadharNumber());
        if (studentAadhar != null) {
            aadharNumbersInFile.add(studentAadhar);
        }
        String documentAadhar = studentAdmissionValidator.resolveDocumentAadhar(
                request.getStudent(), request.getDocuments());
        if (documentAadhar != null) {
            aadharNumbersInFile.add(documentAadhar);
        }
    }

    private StudentAdmissionUploadRowResultDTO parseFailure(ParsedStudentAdmissionRow parsedRow, boolean dryRun) {
        String admissionNo = null;
        return StudentAdmissionUploadRowResultDTO.builder()
                .rowNumber(parsedRow.getRowNumber())
                .admissionNo(admissionNo)
                .success(false)
                .dryRun(dryRun)
                .message(MessageConstants.VALIDATION_FAILED)
                .errors(parsedRow.getErrors())
                .build();
    }

    private StudentAdmissionUploadRowResultDTO rowFailure(
            int rowNumber, StudentAdmissionRequestDTO request, boolean dryRun, List<String> errors) {
        return StudentAdmissionUploadRowResultDTO.builder()
                .rowNumber(rowNumber)
                .admissionNo(request.getStudent().getAdmissionNo())
                .success(false)
                .dryRun(dryRun)
                .message(MessageConstants.VALIDATION_FAILED)
                .errors(errors)
                .build();
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
