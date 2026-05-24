package com.infiniteVision.schoolProject.modules.scholarship.mapper;

import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipApplicationResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.entity.StudentScholarshipApplication;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import org.springframework.stereotype.Component;

/**
 * Maps scholarship application entities to API DTOs.
 */
@Component
public class ScholarshipApplicationMapper {

    public ScholarshipApplicationResponseDTO toResponse(StudentScholarshipApplication application) {
        Student student = application.getStudent();
        String studentName = student != null
                ? (student.getFirstName()
                        + (student.getLastName() != null && !student.getLastName().isBlank()
                                ? " " + student.getLastName()
                                : ""))
                : null;
        return ScholarshipApplicationResponseDTO.builder()
                .applicationId(application.getId())
                .studentId(student != null ? student.getId() : null)
                .admissionNo(student != null ? student.getAdmissionNo() : null)
                .studentName(studentName != null ? studentName.trim() : null)
                .schemeId(application.getScheme() != null ? application.getScheme().getId() : null)
                .schemeName(application.getScheme() != null ? application.getScheme().getSchemeName() : null)
                .academicYearId(
                        application.getAcademicYear() != null ? application.getAcademicYear().getId() : null)
                .academicYearName(
                        application.getAcademicYear() != null
                                ? application.getAcademicYear().getYearName()
                                : null)
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .applicationRemarks(application.getApplicationRemarks())
                .principalApprovedBy(application.getPrincipalApprovedBy())
                .principalApprovedAt(application.getPrincipalApprovedAt())
                .correspondentApprovedBy(application.getCorrespondentApprovedBy())
                .correspondentApprovedAt(application.getCorrespondentApprovedAt())
                .rejectedBy(application.getRejectedBy())
                .rejectedAt(application.getRejectedAt())
                .rejectionReason(application.getRejectionReason())
                .build();
    }
}
