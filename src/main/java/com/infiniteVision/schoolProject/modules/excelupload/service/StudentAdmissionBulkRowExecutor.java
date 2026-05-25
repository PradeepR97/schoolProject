package com.infiniteVision.schoolProject.modules.excelupload.service;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.excelupload.dto.response.StudentAdmissionUploadRowResultDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentAdmissionResponseDTO;
import com.infiniteVision.schoolProject.modules.student.service.StudentAdmissionService;
import com.infiniteVision.schoolProject.modules.student.validator.StudentAdmissionValidator;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Processes one admission row for Excel bulk upload.
 * <p>
 * No class-level transaction: {@link StudentAdmissionService#admitStudent} commits per row.
 * Catching {@link ValidationException} inside a {@code REQUIRES_NEW} boundary caused
 * rollback-only commits ({@code UnexpectedRollbackException}).
 */
@Service
@RequiredArgsConstructor
public class StudentAdmissionBulkRowExecutor {

    private final StudentAdmissionService studentAdmissionService;
    private final StudentAdmissionValidator studentAdmissionValidator;

    public StudentAdmissionUploadRowResultDTO processRow(
            StudentAdmissionRequestDTO request, int rowNumber, boolean dryRun) {
        String admissionNo = request.getStudent().getAdmissionNo();
        try {
            studentAdmissionValidator.validate(request);
            if (dryRun) {
                return StudentAdmissionUploadRowResultDTO.builder()
                        .rowNumber(rowNumber)
                        .admissionNo(admissionNo)
                        .success(true)
                        .dryRun(true)
                        .message(MessageConstants.EXCEL_UPLOAD_DRY_RUN_SUCCESS)
                        .build();
            }
            StudentAdmissionResponseDTO response = studentAdmissionService.admitStudent(request);
            return StudentAdmissionUploadRowResultDTO.builder()
                    .rowNumber(rowNumber)
                    .admissionNo(admissionNo)
                    .success(true)
                    .dryRun(false)
                    .studentId(response.getStudentId())
                    .message(MessageConstants.STUDENT_ADMITTED_SUCCESS)
                    .build();
        } catch (ValidationException ex) {
            return failure(rowNumber, admissionNo, dryRun, mergeErrors(ex));
        } catch (RuntimeException ex) {
            List<String> errors = new ArrayList<>();
            errors.add(ex.getMessage() != null ? ex.getMessage() : MessageConstants.BUSINESS_ERROR);
            return failure(rowNumber, admissionNo, dryRun, errors);
        }
    }

    private static List<String> mergeErrors(ValidationException ex) {
        List<String> errors = new ArrayList<>(ex.getErrors());
        if (errors.isEmpty() && ex.getMessage() != null) {
            errors.add(ex.getMessage());
        }
        return errors;
    }

    private static StudentAdmissionUploadRowResultDTO failure(
            int rowNumber, String admissionNo, boolean dryRun, List<String> errors) {
        return StudentAdmissionUploadRowResultDTO.builder()
                .rowNumber(rowNumber)
                .admissionNo(admissionNo)
                .success(false)
                .dryRun(dryRun)
                .message(MessageConstants.VALIDATION_FAILED)
                .errors(errors)
                .build();
    }
}
