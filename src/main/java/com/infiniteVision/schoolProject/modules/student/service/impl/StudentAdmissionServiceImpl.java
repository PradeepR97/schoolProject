package com.infiniteVision.schoolProject.modules.student.service.impl;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentAdmissionResponseDTO;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.entity.StudentDocument;
import com.infiniteVision.schoolProject.modules.student.entity.StudentParent;
import com.infiniteVision.schoolProject.modules.student.mapper.StudentAdmissionMapper;
import com.infiniteVision.schoolProject.modules.student.repository.StudentRepository;
import com.infiniteVision.schoolProject.modules.student.service.StudentAdmissionService;
import com.infiniteVision.schoolProject.modules.student.validator.StudentAdmissionValidator;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persists student admission as one aggregate: {@link Student}, {@link StudentParent}, {@link StudentDocument}.
 * <p>
 * Validates academic year/class alignment, uniqueness, and parent rules before save.
 * Cascade on {@link Student} persists child rows; {@code uploaded_by} on documents uses the caller.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentAdmissionServiceImpl implements StudentAdmissionService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentAdmissionValidator studentAdmissionValidator;
    private final StudentAdmissionMapper studentAdmissionMapper;

    /**
     * Validates request, builds entities, links one-to-one children, saves student (cascade).
     */
    @Override
    @Transactional
    public StudentAdmissionResponseDTO admitStudent(StudentAdmissionRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        studentAdmissionValidator.validate(request);

        User uploadedBy = userRepository
                .findByIdAndDeletedFalse(caller.getUserId())
                .orElse(null);

        Student student = studentAdmissionMapper.toStudent(request.getStudent());
        StudentParent parents = studentAdmissionMapper.toStudentParent(request.getParents());
        StudentDocument documents = studentAdmissionMapper.toStudentDocument(request, uploadedBy);

        parents.setStudent(student);
        documents.setStudent(student);
        student.setParents(parents);
        student.setDocuments(documents);

        Student saved = studentRepository.save(student);
        log.info(
                "Student admitted id={}, admissionNo={}, by user id={}",
                saved.getId(),
                saved.getAdmissionNo(),
                caller.getUserId());

        return studentAdmissionMapper.toResponse(saved);
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
