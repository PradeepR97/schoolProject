package com.infiniteVision.schoolProject.modules.student.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.student.constants.StudentApiConstants;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentAdmissionResponseDTO;
import com.infiniteVision.schoolProject.modules.student.service.StudentAdmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Student admission endpoints. Business logic lives in {@link StudentAdmissionService}.
 */
@RestController
@RequestMapping(value = StudentApiConstants.STUDENT_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StudentAdmissionController {

    private final StudentAdmissionService studentAdmissionService;

    /** POST /api/v1/students/admissions — create student with parents and documents. */
    @PostMapping("/admissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<StudentAdmissionResponseDTO>> admitStudent(
            @Valid @RequestBody StudentAdmissionRequestDTO request) {
        StudentAdmissionResponseDTO data = studentAdmissionService.admitStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.STUDENT_ADMITTED_SUCCESS, data));
    }
}
