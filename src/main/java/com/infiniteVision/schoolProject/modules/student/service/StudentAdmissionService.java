package com.infiniteVision.schoolProject.modules.student.service;

import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentAdmissionResponseDTO;

/**
 * Student admission use cases (create student with parents and documents in one transaction).
 */
public interface StudentAdmissionService {

    /**
     * Admits a new student with parent/guardian and document metadata.
     *
     * @param request nested student, parents, and optional documents
     * @return created record ids and summary profile
     * @throws com.infiniteVision.schoolProject.exception.ValidationException
     *         business rule or duplicate violations
     * @throws com.infiniteVision.schoolProject.exception.UnauthorizedException
     *         if caller is not authenticated
     */
    StudentAdmissionResponseDTO admitStudent(StudentAdmissionRequestDTO request);
}
