package com.infiniteVision.schoolProject.modules.student.mapper;

import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.academic.util.ClassSectionDisplayFormatter;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.entity.StudentParent;
import com.infiniteVision.schoolProject.modules.student.enums.PrimaryContact;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Maps {@link Student} entities to {@link StudentListItemResponseDTO} for list views.
 */
@Component
public class StudentListMapper {

    /**
     * Builds one list row using optional class label and parent contact from {@code primaryContact}.
     */
    public StudentListItemResponseDTO toListItem(Student student, String className) {
        StudentParent parents = student.getParents();
        return StudentListItemResponseDTO.builder()
                .studentId(student.getId())
                .studentIdCardNo(student.getStudentIdCardNo())
                .studentName(buildStudentName(student.getFirstName(), student.getLastName()))
                .className(className)
                .parentName(resolveParentName(parents))
                .parentPhone(resolveParentPhone(parents))
                .feesPaymentStatus(student.getFeesPaymentStatus())
                .build();
    }

    /**
     * Formats grade + section display label, e.g. {@code Class 10 - A}.
     */
    public String formatClassName(ClassMaster classMaster, SectionMaster section) {
        return ClassSectionDisplayFormatter.format(classMaster, section);
    }

    /**
     * Builds a map of student id to display label for batch lookups on a page of students.
     */
    public Map<Long, String> toClassNameByStudentId(
            Map<Long, ClassMaster> classMasterById, Map<Long, SectionMaster> sectionById, java.util.List<Student> students) {
        return students.stream()
                .filter(student -> student.getClassId() != null)
                .collect(java.util.stream.Collectors.toMap(
                        Student::getId,
                        student -> formatClassName(
                                classMasterById.get(student.getClassId()),
                                sectionById.get(student.getSectionId())),
                        (first, second) -> first));
    }

    private static String buildStudentName(String firstName, String lastName) {
        if (lastName == null || lastName.isBlank()) {
            return firstName != null ? firstName.trim() : "";
        }
        return firstName.trim() + " " + lastName.trim();
    }

    private static String resolveParentName(StudentParent parents) {
        if (parents == null || parents.getPrimaryContact() == null) {
            return null;
        }
        return switch (parents.getPrimaryContact()) {
            case FATHER -> parents.getFatherName();
            case MOTHER -> parents.getMotherName();
            case GUARDIAN -> parents.getGuardianName();
        };
    }

    private static String resolveParentPhone(StudentParent parents) {
        if (parents == null || parents.getPrimaryContact() == null) {
            return null;
        }
        return switch (parents.getPrimaryContact()) {
            case FATHER -> parents.getFatherPhone();
            case MOTHER -> parents.getMotherPhone();
            case GUARDIAN -> parents.getGuardianPhone();
        };
    }
}
