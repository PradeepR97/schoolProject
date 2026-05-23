package com.infiniteVision.schoolProject.modules.student.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentDetailResponseDTO;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.entity.StudentDocument;
import com.infiniteVision.schoolProject.modules.student.entity.StudentParent;
import com.infiniteVision.schoolProject.modules.student.mapper.StudentDetailMapper;
import com.infiniteVision.schoolProject.modules.student.repository.StudentRepository;
import com.infiniteVision.schoolProject.modules.student.service.StudentService;
import com.infiniteVision.schoolProject.modules.student.validator.StudentUpdateValidator;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Student get, update, and soft delete.
 * <p>
 * Loads the student aggregate with parents and documents; update applies only sent fields;
 * delete marks student, parents, and documents deleted in one transaction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentDetailMapper studentDetailMapper;
    private final StudentUpdateValidator studentUpdateValidator;
    private final AuditService auditService;

    /**
     * Loads active student with parents and documents and maps to detail DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public StudentDetailResponseDTO getStudentById(Long id) {
        Student student = findActiveWithRelationsOrThrow(id);
        return studentDetailMapper.toDetail(student);
    }

    /**
     * Validates partial update, applies changes to aggregate, saves, returns detail.
     */
    @Override
    @Transactional
    public StudentDetailResponseDTO updateStudent(Long id, UpdateStudentRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        Student student = findActiveWithRelationsOrThrow(id);
        StudentDetailResponseDTO beforeSnapshot = studentDetailMapper.toDetail(student);
        StudentParent parents = student.getParents();
        StudentDocument documents = student.getDocuments();

        studentUpdateValidator.validateUpdate(id, request, student, parents, documents);

        if (request.getStudent() != null) {
            studentDetailMapper.applyProfileUpdates(student, request.getStudent());
        }
        if (request.getParents() != null && parents != null) {
            studentDetailMapper.applyParentUpdates(parents, request.getParents());
        }
        if (request.getDocuments() != null && documents != null) {
            studentDetailMapper.applyDocumentUpdates(documents, request.getDocuments());
        }

        Student saved = studentRepository.save(student);
        StudentDetailResponseDTO afterSnapshot = studentDetailMapper.toDetail(saved);
        auditService.logUpdate(AuditEntityType.STUDENT, id, beforeSnapshot, afterSnapshot);
        log.info("Student updated id={} by user id={}", id, caller.getUserId());
        return afterSnapshot;
    }

    /**
     * Soft-deletes student, parents, and documents; sets student status to DISCONTINUED.
     */
    @Override
    @Transactional
    public void deleteStudent(Long id) {
        AuthenticatedUser caller = currentUser();
        Student student = findActiveWithRelationsOrThrow(id);
        StudentDetailResponseDTO beforeSnapshot = studentDetailMapper.toDetail(student);

        student.softDelete(caller.getUserId());
        if (student.getParents() != null) {
            student.getParents().markDeleted(caller.getUserId());
        }
        if (student.getDocuments() != null) {
            student.getDocuments().markDeleted(caller.getUserId());
        }

        studentRepository.save(student);
        auditService.logDelete(AuditEntityType.STUDENT, id, beforeSnapshot);
        log.info("Student soft-deleted id={} by user id={}", id, caller.getUserId());
    }

    private Student findActiveWithRelationsOrThrow(Long id) {
        return studentRepository
                .findActiveWithParentsAndDocumentsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.STUDENT_NOT_FOUND));
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
