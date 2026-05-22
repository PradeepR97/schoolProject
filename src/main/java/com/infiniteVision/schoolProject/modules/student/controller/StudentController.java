package com.infiniteVision.schoolProject.modules.student.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.student.constants.StudentApiConstants;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.student.service.StudentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Student read APIs. Writes (admission) live in {@link StudentAdmissionController}.
 */
@RestController
@RequestMapping(value = StudentApiConstants.STUDENT_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StudentController {

    private final StudentQueryService studentQueryService;

    /**
     * GET /api/v1/students — paginated list (default 10 per page).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<PagedResponseDTO<StudentListItemResponseDTO>>> listStudents(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PagedResponseDTO<StudentListItemResponseDTO> data = studentQueryService.listStudents(page, size);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.STUDENTS_LISTED_SUCCESS, data));
    }
}
