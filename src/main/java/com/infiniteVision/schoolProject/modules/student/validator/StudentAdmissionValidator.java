package com.infiniteVision.schoolProject.modules.student.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionDocumentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionParentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionStudentDTO;
import com.infiniteVision.schoolProject.modules.student.enums.PrimaryContact;
import com.infiniteVision.schoolProject.modules.student.repository.StudentDocumentRepository;
import com.infiniteVision.schoolProject.modules.student.repository.StudentRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Business validation for student admission (uniqueness, academic references, parent rules).
 */
@Component
@RequiredArgsConstructor
public class StudentAdmissionValidator {

    private final StudentRepository studentRepository;
    private final StudentDocumentRepository studentDocumentRepository;
    private final ClassMasterRepository classMasterRepository;
    private final AcademicYearRepository academicYearRepository;

    /**
     * Validates admission payload before persistence.
     */
    public void validate(StudentAdmissionRequestDTO request) {
        List<String> errors = new ArrayList<>();
        StudentAdmissionStudentDTO student = request.getStudent();
        StudentAdmissionParentsDTO parents = request.getParents();
        StudentAdmissionDocumentsDTO documents = request.getDocuments();

        validateAcademicReferences(student, errors);
        validateUniqueness(student, documents, errors);
        validateAadharAlignment(student, documents, errors);
        validateParents(parents, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    private void validateAcademicReferences(StudentAdmissionStudentDTO student, List<String> errors) {
        if (!academicYearRepository.findByIdAndDeletedFalse(student.getAcademicYearId()).isPresent()) {
            errors.add(MessageConstants.ACADEMIC_YEAR_NOT_FOUND);
            return;
        }

        ClassMaster classMaster = classMasterRepository
                .findByIdAndDeletedFalse(student.getClassId())
                .orElse(null);
        if (classMaster == null) {
            errors.add(MessageConstants.CLASS_NOT_FOUND);
            return;
        }

        if (!student.getAcademicYearId().equals(classMaster.getAcademicYear().getId())) {
            errors.add(MessageConstants.CLASS_ACADEMIC_YEAR_MISMATCH);
        }
    }

    private void validateUniqueness(
            StudentAdmissionStudentDTO student,
            StudentAdmissionDocumentsDTO documents,
            List<String> errors) {
        if (studentRepository.existsByAdmissionNoAndDeletedFalse(student.getAdmissionNo().trim())) {
            errors.add(MessageConstants.ADMISSION_NO_ALREADY_EXISTS);
        }

        String aadharNumber = trimToNull(student.getAadharNumber());
        if (aadharNumber != null && studentRepository.existsByAadharNumberAndDeletedFalse(aadharNumber)) {
            errors.add(MessageConstants.STUDENT_AADHAR_ALREADY_EXISTS);
        }

        String documentAadhar = resolveDocumentAadhar(student, documents);
        if (documentAadhar != null && studentDocumentRepository.existsByAadharNoAndDeletedFalse(documentAadhar)) {
            errors.add(MessageConstants.DOCUMENT_AADHAR_ALREADY_EXISTS);
        }
    }

    private void validateAadharAlignment(
            StudentAdmissionStudentDTO student,
            StudentAdmissionDocumentsDTO documents,
            List<String> errors) {
        String studentAadhar = trimToNull(student.getAadharNumber());
        String documentAadhar =
                documents != null ? trimToNull(documents.getAadharNo()) : null;

        if (studentAadhar != null && documentAadhar != null && !studentAadhar.equals(documentAadhar)) {
            errors.add(MessageConstants.AADHAR_MISMATCH);
        }
    }

    private void validateParents(StudentAdmissionParentsDTO parents, List<String> errors) {
        boolean hasFather = isNotBlank(parents.getFatherName());
        boolean hasMother = isNotBlank(parents.getMotherName());
        boolean hasGuardian = isNotBlank(parents.getGuardianName());

        if (!hasFather && !hasMother && !hasGuardian) {
            errors.add(MessageConstants.PARENT_CONTACT_REQUIRED);
        }

        if (PrimaryContact.GUARDIAN.equals(parents.getPrimaryContact())) {
            if (!hasGuardian || !isNotBlank(parents.getGuardianPhone())) {
                errors.add(MessageConstants.GUARDIAN_DETAILS_REQUIRED);
            }
        }
    }

    /**
     * Resolves document Aadhar from request documents or student profile.
     */
    public String resolveDocumentAadhar(
            StudentAdmissionStudentDTO student, StudentAdmissionDocumentsDTO documents) {
        if (documents != null && isNotBlank(documents.getAadharNo())) {
            return documents.getAadharNo().trim();
        }
        return trimToNull(student.getAadharNumber());
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
