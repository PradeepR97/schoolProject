package com.infiniteVision.schoolProject.modules.student.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.student.constants.StudentApiConstants;
import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.student.service.StudentQueryService;
import com.infiniteVision.schoolProject.modules.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Student read and management APIs. Admission create lives in {@link StudentAdmissionController}.
 */
@RestController
@RequestMapping(value = StudentApiConstants.STUDENT_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StudentController {

    private final StudentQueryService studentQueryService;
    private final StudentService studentService;

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

    /**
     * GET /api/v1/students/{id} — full student detail with parents and documents.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<StudentDetailResponseDTO>> getStudentById(@PathVariable Long id) {
        StudentDetailResponseDTO data = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.STUDENT_RETRIEVED_SUCCESS, data));
    }

    /**
     * PUT /api/v1/students/{id} — partial update of student aggregate.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<StudentDetailResponseDTO>> updateStudent(
            @PathVariable Long id, @Valid @RequestBody UpdateStudentRequestDTO request) {
        StudentDetailResponseDTO data = studentService.updateStudent(id, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.STUDENT_UPDATED_SUCCESS, data));
    }

    /**
     * DELETE /api/v1/students/{id} — soft delete student, parents, and documents.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT')")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.STUDENT_DELETED_SUCCESS));
    }
}
