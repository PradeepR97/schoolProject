package com.infiniteVision.schoolProject.modules.student.service;

import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentDetailResponseDTO;

/**
 * Student lifecycle operations: get detail, partial update, and soft delete.
 */
public interface StudentService {

    /**
     * Returns one active student with parents and documents.
     *
     * @param id student primary key
     * @return full student detail
     */
    StudentDetailResponseDTO getStudentById(Long id);

    /**
     * Partially updates student profile, parents, and/or documents.
     *
     * @param id student primary key
     * @param request fields to apply
     * @return updated student detail
     */
    StudentDetailResponseDTO updateStudent(Long id, UpdateStudentRequestDTO request);

    /**
     * Soft-deletes student and linked parent/document rows.
     *
     * @param id student primary key
     */
    void deleteStudent(Long id);
}
