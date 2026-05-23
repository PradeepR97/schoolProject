package com.infiniteVision.schoolProject.modules.student.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionParentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentDocumentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentParentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentProfileDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentRequestDTO;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.entity.StudentDocument;
import com.infiniteVision.schoolProject.modules.student.entity.StudentParent;
import com.infiniteVision.schoolProject.modules.student.enums.PrimaryContact;
import com.infiniteVision.schoolProject.modules.student.repository.StudentDocumentRepository;
import com.infiniteVision.schoolProject.modules.student.repository.StudentRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Business validation for student update (partial fields, uniqueness, academic and parent rules).
 */
@Component
@RequiredArgsConstructor
public class StudentUpdateValidator {

    private final StudentRepository studentRepository;
    private final StudentDocumentRepository studentDocumentRepository;
    private final ClassMasterRepository classMasterRepository;
    private final AcademicYearRepository academicYearRepository;

    /**
     * Validates update payload against current aggregate state.
     */
    public void validateUpdate(
            Long studentId,
            UpdateStudentRequestDTO request,
            Student student,
            StudentParent parents,
            StudentDocument documents) {
        List<String> errors = new ArrayList<>();

        if (!hasAnyField(request)) {
            errors.add(MessageConstants.STUDENT_UPDATE_EMPTY);
        }

        UpdateStudentProfileDTO profile = request.getStudent();
        if (profile != null) {
            validateAcademicReferences(profile, student, errors);
            validateUniqueness(studentId, profile, request.getDocuments(), documents, errors);
            validateAadharAlignment(profile, request.getDocuments(), student, documents, errors);
        } else if (request.getDocuments() != null) {
            validateDocumentAadharUniqueness(studentId, request.getDocuments(), documents, errors);
            validateAadharAlignment(null, request.getDocuments(), student, documents, errors);
        }

        if (request.getParents() != null && parents != null) {
            validateParents(request.getParents(), parents, errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    private void validateAcademicReferences(
            UpdateStudentProfileDTO profile, Student student, List<String> errors) {
        Long academicYearId = profile.getAcademicYearId() != null ? profile.getAcademicYearId() : student.getAcademicYearId();
        Long classId = profile.getClassId() != null ? profile.getClassId() : student.getClassId();

        if (profile.getAcademicYearId() != null
                && !academicYearRepository.findByIdAndDeletedFalse(profile.getAcademicYearId()).isPresent()) {
            errors.add(MessageConstants.ACADEMIC_YEAR_NOT_FOUND);
            return;
        }

        if (profile.getClassId() != null) {
            ClassMaster classMaster = classMasterRepository.findByIdAndDeletedFalse(classId).orElse(null);
            if (classMaster == null) {
                errors.add(MessageConstants.CLASS_NOT_FOUND);
                return;
            }
            if (!academicYearId.equals(classMaster.getAcademicYear().getId())) {
                errors.add(MessageConstants.CLASS_ACADEMIC_YEAR_MISMATCH);
            }
        } else if (profile.getAcademicYearId() != null && student.getClassId() != null) {
            ClassMaster classMaster = classMasterRepository
                    .findByIdAndDeletedFalse(student.getClassId())
                    .orElse(null);
            if (classMaster != null && !profile.getAcademicYearId().equals(classMaster.getAcademicYear().getId())) {
                errors.add(MessageConstants.CLASS_ACADEMIC_YEAR_MISMATCH);
            }
        }
    }

    private void validateUniqueness(
            Long studentId,
            UpdateStudentProfileDTO profile,
            UpdateStudentDocumentsDTO documentsDto,
            StudentDocument documents,
            List<String> errors) {
        String aadharNumber = trimToNull(profile.getAadharNumber());
        if (aadharNumber != null && studentRepository.existsByAadharNumberAndIdNotAndDeletedFalse(aadharNumber, studentId)) {
            errors.add(MessageConstants.STUDENT_AADHAR_ALREADY_EXISTS);
        }

        String applicationNumber = trimToNull(profile.getApplicationNumber());
        if (applicationNumber != null
                && studentRepository.existsByApplicationNumberAndIdNotAndDeletedFalse(applicationNumber, studentId)) {
            errors.add("Application number is already registered");
        }

        String idCardNo = trimToNull(profile.getStudentIdCardNo());
        if (idCardNo != null
                && studentRepository.existsByStudentIdCardNoAndIdNotAndDeletedFalse(idCardNo, studentId)) {
            errors.add("Student ID card number is already registered");
        }

        validateDocumentAadharUniqueness(studentId, documentsDto, documents, errors);
    }

    private void validateDocumentAadharUniqueness(
            Long studentId,
            UpdateStudentDocumentsDTO documentsDto,
            StudentDocument documents,
            List<String> errors) {
        if (documentsDto == null || documents == null) {
            return;
        }
        String documentAadhar = trimToNull(documentsDto.getAadharNo());
        if (documentAadhar != null
                && studentDocumentRepository.existsByAadharNoAndIdNotAndDeletedFalse(
                        documentAadhar, documents.getId())) {
            errors.add(MessageConstants.DOCUMENT_AADHAR_ALREADY_EXISTS);
        }
    }

    private void validateAadharAlignment(
            UpdateStudentProfileDTO profile,
            UpdateStudentDocumentsDTO documentsDto,
            Student student,
            StudentDocument documents,
            List<String> errors) {
        String studentAadhar = profile != null ? trimToNull(profile.getAadharNumber()) : null;
        if (studentAadhar == null && student != null) {
            studentAadhar = trimToNull(student.getAadharNumber());
        }

        String documentAadhar = documentsDto != null ? trimToNull(documentsDto.getAadharNo()) : null;
        if (documentAadhar == null && documents != null) {
            documentAadhar = trimToNull(documents.getAadharNo());
        }

        if (studentAadhar != null && documentAadhar != null && !studentAadhar.equals(documentAadhar)) {
            errors.add(MessageConstants.AADHAR_MISMATCH);
        }
    }

    private void validateParents(
            UpdateStudentParentsDTO update, StudentParent parents, List<String> errors) {
        StudentAdmissionParentsDTO merged = StudentAdmissionParentsDTO.builder()
                .fatherName(coalesce(update.getFatherName(), parents.getFatherName()))
                .fatherPhone(coalesce(update.getFatherPhone(), parents.getFatherPhone()))
                .fatherEmail(coalesce(update.getFatherEmail(), parents.getFatherEmail()))
                .fatherOccupation(coalesce(update.getFatherOccupation(), parents.getFatherOccupation()))
                .fatherAnnualIncome(
                        update.getFatherAnnualIncome() != null
                                ? update.getFatherAnnualIncome()
                                : parents.getFatherAnnualIncome())
                .motherName(coalesce(update.getMotherName(), parents.getMotherName()))
                .motherPhone(coalesce(update.getMotherPhone(), parents.getMotherPhone()))
                .motherEmail(coalesce(update.getMotherEmail(), parents.getMotherEmail()))
                .motherOccupation(coalesce(update.getMotherOccupation(), parents.getMotherOccupation()))
                .motherAnnualIncome(
                        update.getMotherAnnualIncome() != null
                                ? update.getMotherAnnualIncome()
                                : parents.getMotherAnnualIncome())
                .guardianName(coalesce(update.getGuardianName(), parents.getGuardianName()))
                .guardianPhone(coalesce(update.getGuardianPhone(), parents.getGuardianPhone()))
                .guardianEmail(coalesce(update.getGuardianEmail(), parents.getGuardianEmail()))
                .guardianOccupation(coalesce(update.getGuardianOccupation(), parents.getGuardianOccupation()))
                .guardianRelationship(coalesce(update.getGuardianRelationship(), parents.getGuardianRelationship()))
                .primaryContact(
                        update.getPrimaryContact() != null ? update.getPrimaryContact() : parents.getPrimaryContact())
                .build();

        boolean hasFather = isNotBlank(merged.getFatherName());
        boolean hasMother = isNotBlank(merged.getMotherName());
        boolean hasGuardian = isNotBlank(merged.getGuardianName());

        if (!hasFather && !hasMother && !hasGuardian) {
            errors.add(MessageConstants.PARENT_CONTACT_REQUIRED);
        }

        if (PrimaryContact.GUARDIAN.equals(merged.getPrimaryContact())) {
            if (!hasGuardian || !isNotBlank(merged.getGuardianPhone())) {
                errors.add(MessageConstants.GUARDIAN_DETAILS_REQUIRED);
            }
        }
    }

    private static boolean hasAnyField(UpdateStudentRequestDTO request) {
        if (request.getStudent() != null && hasAnyProfileField(request.getStudent())) {
            return true;
        }
        if (request.getParents() != null && hasAnyParentField(request.getParents())) {
            return true;
        }
        return request.getDocuments() != null && hasAnyDocumentField(request.getDocuments());
    }

    private static boolean hasAnyProfileField(UpdateStudentProfileDTO profile) {
        return profile.getAadharNumber() != null
                || profile.getEmisNumber() != null
                || profile.getRationCardNumber() != null
                || profile.getApplicationNumber() != null
                || profile.getStudentIdCardNo() != null
                || profile.getFirstName() != null
                || profile.getLastName() != null
                || profile.getDateOfBirth() != null
                || profile.getGender() != null
                || profile.getNationality() != null
                || profile.getMotherTongue() != null
                || profile.getStudyGroup() != null
                || profile.getTenthMark() != null
                || profile.getIdentificationMark1() != null
                || profile.getIdentificationMark2() != null
                || profile.getAddress() != null
                || profile.getClassId() != null
                || profile.getAcademicYearId() != null
                || profile.getBloodGroup() != null
                || profile.getReligion() != null
                || profile.getCommunity() != null
                || profile.getAnnualIncome() != null
                || profile.getDifferentlyAbled() != null
                || profile.getDisabilityType() != null
                || profile.getDisabilityPercentage() != null
                || profile.getStatus() != null
                || profile.getFeesPaymentStatus() != null;
    }

    private static boolean hasAnyParentField(UpdateStudentParentsDTO parents) {
        return parents.getFatherName() != null
                || parents.getFatherPhone() != null
                || parents.getFatherEmail() != null
                || parents.getFatherOccupation() != null
                || parents.getFatherAnnualIncome() != null
                || parents.getFatherQualification() != null
                || parents.getMotherName() != null
                || parents.getMotherPhone() != null
                || parents.getMotherEmail() != null
                || parents.getMotherOccupation() != null
                || parents.getMotherAnnualIncome() != null
                || parents.getMotherQualification() != null
                || parents.getGuardianName() != null
                || parents.getGuardianPhone() != null
                || parents.getGuardianEmail() != null
                || parents.getGuardianOccupation() != null
                || parents.getGuardianQualification() != null
                || parents.getGuardianRelationship() != null
                || parents.getPrimaryContact() != null;
    }

    private static boolean hasAnyDocumentField(UpdateStudentDocumentsDTO documents) {
        return documents.getProfilePhotoUrl() != null
                || documents.getAadharNo() != null
                || documents.getAadharFileUrl() != null;
    }

    private static String coalesce(String updateValue, String existingValue) {
        return updateValue != null ? trimToNull(updateValue) : existingValue;
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
