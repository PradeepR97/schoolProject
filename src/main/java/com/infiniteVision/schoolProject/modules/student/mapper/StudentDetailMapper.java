package com.infiniteVision.schoolProject.modules.student.mapper;

import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentDocumentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentParentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.UpdateStudentProfileDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentDocumentsResponseDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentParentsResponseDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentProfileResponseDTO;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.entity.StudentDocument;
import com.infiniteVision.schoolProject.modules.student.entity.StudentParent;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Maps student aggregate to detail DTOs and applies partial updates.
 */
@Component
@RequiredArgsConstructor
public class StudentDetailMapper {

    private final ClassMasterRepository classMasterRepository;
    private final StudentListMapper studentListMapper;

    public StudentDetailResponseDTO toDetail(Student student) {
        String className = resolveClassName(student.getClassId());
        return StudentDetailResponseDTO.builder()
                .student(toProfile(student, className))
                .parents(toParents(student.getParents()))
                .documents(toDocuments(student.getDocuments()))
                .build();
    }

    public void applyProfileUpdates(Student student, UpdateStudentProfileDTO dto) {
        if (dto.getAadharNumber() != null) {
            student.setAadharNumber(trimToNull(dto.getAadharNumber()));
        }
        if (dto.getEmisNumber() != null) {
            student.setEmisNumber(trimToNull(dto.getEmisNumber()));
        }
        if (dto.getRationCardNumber() != null) {
            student.setRationCardNumber(trimToNull(dto.getRationCardNumber()));
        }
        if (dto.getApplicationNumber() != null) {
            student.setApplicationNumber(trimToNull(dto.getApplicationNumber()));
        }
        if (dto.getStudentIdCardNo() != null) {
            student.setStudentIdCardNo(trimToNull(dto.getStudentIdCardNo()));
        }
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            student.setFirstName(dto.getFirstName().trim());
        }
        if (dto.getLastName() != null) {
            student.setLastName(trimToNull(dto.getLastName()));
        }
        if (dto.getDateOfBirth() != null) {
            student.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getGender() != null) {
            student.setGender(dto.getGender());
        }
        if (dto.getNationality() != null) {
            student.setNationality(trimToNull(dto.getNationality()));
        }
        if (dto.getMotherTongue() != null) {
            student.setMotherTongue(trimToNull(dto.getMotherTongue()));
        }
        if (dto.getStudyGroup() != null) {
            student.setStudyGroup(trimToNull(dto.getStudyGroup()));
        }
        if (dto.getTenthMark() != null) {
            student.setTenthMark(dto.getTenthMark());
        }
        if (dto.getIdentificationMark1() != null) {
            student.setIdentificationMark1(trimToNull(dto.getIdentificationMark1()));
        }
        if (dto.getIdentificationMark2() != null) {
            student.setIdentificationMark2(trimToNull(dto.getIdentificationMark2()));
        }
        if (dto.getAddress() != null) {
            student.setAddress(trimToNull(dto.getAddress()));
        }
        if (dto.getClassId() != null) {
            student.setClassId(dto.getClassId());
        }
        if (dto.getAcademicYearId() != null) {
            student.setAcademicYearId(dto.getAcademicYearId());
        }
        if (dto.getBloodGroup() != null) {
            student.setBloodGroup(dto.getBloodGroup());
        }
        if (dto.getReligion() != null) {
            student.setReligion(dto.getReligion());
        }
        if (dto.getCommunity() != null) {
            student.setCommunity(dto.getCommunity());
        }
        if (dto.getAnnualIncome() != null) {
            student.setAnnualIncome(dto.getAnnualIncome());
        }
        if (dto.getDifferentlyAbled() != null) {
            student.setDifferentlyAbled(dto.getDifferentlyAbled());
        }
        if (dto.getDisabilityType() != null) {
            student.setDisabilityType(trimToNull(dto.getDisabilityType()));
        }
        if (dto.getDisabilityPercentage() != null) {
            student.setDisabilityPercentage(dto.getDisabilityPercentage());
        }
        if (dto.getStatus() != null) {
            student.setStatus(dto.getStatus());
        }
        if (dto.getFeesPaymentStatus() != null) {
            student.setFeesPaymentStatus(dto.getFeesPaymentStatus());
        }
    }

    public void applyParentUpdates(StudentParent parents, UpdateStudentParentsDTO dto) {
        if (dto.getFatherName() != null) {
            parents.setFatherName(trimToNull(dto.getFatherName()));
        }
        if (dto.getFatherPhone() != null) {
            parents.setFatherPhone(trimToNull(dto.getFatherPhone()));
        }
        if (dto.getFatherEmail() != null) {
            parents.setFatherEmail(trimToNull(dto.getFatherEmail()));
        }
        if (dto.getFatherOccupation() != null) {
            parents.setFatherOccupation(trimToNull(dto.getFatherOccupation()));
        }
        if (dto.getFatherAnnualIncome() != null) {
            parents.setFatherAnnualIncome(dto.getFatherAnnualIncome());
        }
        if (dto.getFatherQualification() != null) {
            parents.setFatherQualification(trimToNull(dto.getFatherQualification()));
        }
        if (dto.getMotherName() != null) {
            parents.setMotherName(trimToNull(dto.getMotherName()));
        }
        if (dto.getMotherPhone() != null) {
            parents.setMotherPhone(trimToNull(dto.getMotherPhone()));
        }
        if (dto.getMotherEmail() != null) {
            parents.setMotherEmail(trimToNull(dto.getMotherEmail()));
        }
        if (dto.getMotherOccupation() != null) {
            parents.setMotherOccupation(trimToNull(dto.getMotherOccupation()));
        }
        if (dto.getMotherAnnualIncome() != null) {
            parents.setMotherAnnualIncome(dto.getMotherAnnualIncome());
        }
        if (dto.getMotherQualification() != null) {
            parents.setMotherQualification(trimToNull(dto.getMotherQualification()));
        }
        if (dto.getGuardianName() != null) {
            parents.setGuardianName(trimToNull(dto.getGuardianName()));
        }
        if (dto.getGuardianPhone() != null) {
            parents.setGuardianPhone(trimToNull(dto.getGuardianPhone()));
        }
        if (dto.getGuardianEmail() != null) {
            parents.setGuardianEmail(trimToNull(dto.getGuardianEmail()));
        }
        if (dto.getGuardianOccupation() != null) {
            parents.setGuardianOccupation(trimToNull(dto.getGuardianOccupation()));
        }
        if (dto.getGuardianQualification() != null) {
            parents.setGuardianQualification(trimToNull(dto.getGuardianQualification()));
        }
        if (dto.getGuardianRelationship() != null) {
            parents.setGuardianRelationship(trimToNull(dto.getGuardianRelationship()));
        }
        if (dto.getPrimaryContact() != null) {
            parents.setPrimaryContact(dto.getPrimaryContact());
        }
    }

    public void applyDocumentUpdates(StudentDocument documents, UpdateStudentDocumentsDTO dto) {
        if (dto.getProfilePhotoUrl() != null) {
            String url = trimToNull(dto.getProfilePhotoUrl());
            documents.setProfilePhotoUrl(url);
            documents.setProfilePhotoUpdatedAt(url != null ? LocalDateTime.now() : null);
        }
        if (dto.getAadharNo() != null) {
            documents.setAadharNo(trimToNull(dto.getAadharNo()));
        }
        if (dto.getAadharFileUrl() != null) {
            documents.setAadharFileUrl(trimToNull(dto.getAadharFileUrl()));
        }
    }

    private StudentProfileResponseDTO toProfile(Student student, String className) {
        return StudentProfileResponseDTO.builder()
                .studentId(student.getId())
                .admissionNo(student.getAdmissionNo())
                .applicationNumber(student.getApplicationNumber())
                .studentIdCardNo(student.getStudentIdCardNo())
                .aadharNumber(student.getAadharNumber())
                .emisNumber(student.getEmisNumber())
                .rationCardNumber(student.getRationCardNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .nationality(student.getNationality())
                .motherTongue(student.getMotherTongue())
                .studyGroup(student.getStudyGroup())
                .tenthMark(student.getTenthMark())
                .identificationMark1(student.getIdentificationMark1())
                .identificationMark2(student.getIdentificationMark2())
                .address(student.getAddress())
                .classId(student.getClassId())
                .className(className)
                .academicYearId(student.getAcademicYearId())
                .bloodGroup(student.getBloodGroup())
                .religion(student.getReligion())
                .community(student.getCommunity())
                .annualIncome(student.getAnnualIncome())
                .differentlyAbled(student.getDifferentlyAbled())
                .disabilityType(student.getDisabilityType())
                .disabilityPercentage(student.getDisabilityPercentage())
                .status(student.getStatus())
                .feesPaymentStatus(student.getFeesPaymentStatus())
                .build();
    }

    private StudentParentsResponseDTO toParents(StudentParent parents) {
        if (parents == null) {
            return null;
        }
        return StudentParentsResponseDTO.builder()
                .parentRecordId(parents.getId())
                .fatherName(parents.getFatherName())
                .fatherPhone(parents.getFatherPhone())
                .fatherEmail(parents.getFatherEmail())
                .fatherOccupation(parents.getFatherOccupation())
                .fatherAnnualIncome(parents.getFatherAnnualIncome())
                .fatherQualification(parents.getFatherQualification())
                .motherName(parents.getMotherName())
                .motherPhone(parents.getMotherPhone())
                .motherEmail(parents.getMotherEmail())
                .motherOccupation(parents.getMotherOccupation())
                .motherAnnualIncome(parents.getMotherAnnualIncome())
                .motherQualification(parents.getMotherQualification())
                .guardianName(parents.getGuardianName())
                .guardianPhone(parents.getGuardianPhone())
                .guardianEmail(parents.getGuardianEmail())
                .guardianOccupation(parents.getGuardianOccupation())
                .guardianQualification(parents.getGuardianQualification())
                .guardianRelationship(parents.getGuardianRelationship())
                .primaryContact(parents.getPrimaryContact())
                .build();
    }

    private StudentDocumentsResponseDTO toDocuments(StudentDocument documents) {
        if (documents == null) {
            return null;
        }
        return StudentDocumentsResponseDTO.builder()
                .documentRecordId(documents.getId())
                .profilePhotoUrl(documents.getProfilePhotoUrl())
                .aadharNo(documents.getAadharNo())
                .aadharFileUrl(documents.getAadharFileUrl())
                .verificationStatus(documents.getVerificationStatus())
                .build();
    }

    private String resolveClassName(Long classId) {
        if (classId == null) {
            return null;
        }
        Optional<ClassMaster> classMaster = classMasterRepository.findByIdAndDeletedFalseWithSection(classId);
        return classMaster.map(studentListMapper::formatClassName).orElse(null);
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
