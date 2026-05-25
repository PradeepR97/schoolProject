package com.infiniteVision.schoolProject.modules.student.mapper;

import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionDocumentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionParentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionStudentDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentAdmissionResponseDTO;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.entity.StudentDocument;
import com.infiniteVision.schoolProject.modules.student.entity.StudentParent;
import com.infiniteVision.schoolProject.modules.student.enums.DocumentVerificationStatus;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;
import com.infiniteVision.schoolProject.modules.student.validator.StudentAdmissionValidator;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * Maps admission DTOs to {@link Student}, {@link StudentParent}, and {@link StudentDocument} entities.
 */
@Component
public class StudentAdmissionMapper {

    private final StudentAdmissionValidator studentAdmissionValidator;

    public StudentAdmissionMapper(StudentAdmissionValidator studentAdmissionValidator) {
        this.studentAdmissionValidator = studentAdmissionValidator;
    }

    public Student toStudent(StudentAdmissionStudentDTO dto) {
        StudentStatus status = dto.getStatus() != null ? dto.getStatus() : StudentStatus.ACTIVE;
        FeesPaymentStatus feesPaymentStatus =
                dto.getFeesPaymentStatus() != null ? dto.getFeesPaymentStatus() : FeesPaymentStatus.PENDING;
        return Student.builder()
                .admissionNo(dto.getAdmissionNo().trim())
                .aadharNumber(trimToNull(dto.getAadharNumber()))
                .emisNumber(trimToNull(dto.getEmisNumber()))
                .rationCardNumber(trimToNull(dto.getRationCardNumber()))
                .firstName(dto.getFirstName().trim())
                .lastName(trimToNull(dto.getLastName()))
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .nationality(trimToNull(dto.getNationality()))
                .medium(dto.getMedium())
                .address(trimToNull(dto.getAddress()))
                .classId(dto.getClassId())
                .academicYearId(dto.getAcademicYearId())
                .bloodGroup(dto.getBloodGroup())
                .religion(dto.getReligion())
                .community(dto.getCommunity())
                .annualIncome(dto.getAnnualIncome())
                .differentlyAbled(Boolean.FALSE)
                .status(status)
                .feesPaymentStatus(feesPaymentStatus)
                .transportRequired(Boolean.TRUE.equals(dto.getTransportRequired()))
                .deleted(Boolean.FALSE)
                .build();
    }

    public StudentParent toStudentParent(StudentAdmissionParentsDTO dto) {
        return StudentParent.builder()
                .fatherName(trimToNull(dto.getFatherName()))
                .fatherPhone(trimToNull(dto.getFatherPhone()))
                .fatherEmail(trimToNull(dto.getFatherEmail()))
                .fatherOccupation(trimToNull(dto.getFatherOccupation()))
                .fatherAnnualIncome(dto.getFatherAnnualIncome())
                .motherName(trimToNull(dto.getMotherName()))
                .motherPhone(trimToNull(dto.getMotherPhone()))
                .motherEmail(trimToNull(dto.getMotherEmail()))
                .motherOccupation(trimToNull(dto.getMotherOccupation()))
                .motherAnnualIncome(dto.getMotherAnnualIncome())
                .guardianName(trimToNull(dto.getGuardianName()))
                .guardianPhone(trimToNull(dto.getGuardianPhone()))
                .guardianEmail(trimToNull(dto.getGuardianEmail()))
                .guardianOccupation(trimToNull(dto.getGuardianOccupation()))
                .guardianRelationship(trimToNull(dto.getGuardianRelationship()))
                .primaryContact(dto.getPrimaryContact())
                .deleted(Boolean.FALSE)
                .build();
    }

    public StudentDocument toStudentDocument(
            StudentAdmissionRequestDTO request, User uploadedBy) {
        StudentAdmissionStudentDTO studentDto = request.getStudent();
        StudentAdmissionDocumentsDTO documentsDto = request.getDocuments();
        String aadharNo = studentAdmissionValidator.resolveDocumentAadhar(studentDto, documentsDto);

        String profilePhotoUrl =
                documentsDto != null ? trimToNull(documentsDto.getProfilePhotoUrl()) : null;
        LocalDateTime profilePhotoUpdatedAt =
                profilePhotoUrl != null ? LocalDateTime.now() : null;

        return StudentDocument.builder()
                .profilePhotoUrl(profilePhotoUrl)
                .profilePhotoUpdatedAt(profilePhotoUpdatedAt)
                .aadharNo(aadharNo)
                .birthCertAvailable(Boolean.FALSE)
                .specialChild(Boolean.FALSE)
                .verificationStatus(DocumentVerificationStatus.UNVERIFIED)
                .uploadedBy(uploadedBy)
                .deleted(Boolean.FALSE)
                .build();
    }

    public StudentAdmissionResponseDTO toResponse(Student student) {
        return StudentAdmissionResponseDTO.builder()
                .studentId(student.getId())
                .admissionNo(student.getAdmissionNo())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .medium(student.getMedium())
                .classId(student.getClassId())
                .academicYearId(student.getAcademicYearId())
                .status(student.getStatus())
                .feesPaymentStatus(student.getFeesPaymentStatus())
                .parentRecordId(student.getParents() != null ? student.getParents().getId() : null)
                .documentRecordId(student.getDocuments() != null ? student.getDocuments().getId() : null)
                .build();
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
