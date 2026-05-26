package com.infiniteVision.schoolProject.modules.academic.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.SectionMasterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validates academic year, grade ({@link ClassMaster}), and section references for enrollment rows.
 */
@Component
@RequiredArgsConstructor
public class AcademicEnrollmentValidator {

    private final AcademicYearRepository academicYearRepository;
    private final ClassMasterRepository classMasterRepository;
    private final SectionMasterRepository sectionMasterRepository;

    /**
     * Ensures year, grade, and section exist and the grade belongs to the academic year.
     */
    public void validateEnrollment(Long academicYearId, Long classId, Long sectionId, List<String> errors) {
        if (!academicYearRepository.findByIdAndDeletedFalse(academicYearId).isPresent()) {
            errors.add(MessageConstants.ACADEMIC_YEAR_NOT_FOUND);
            return;
        }

        ClassMaster classMaster = classMasterRepository.findByIdAndDeletedFalse(classId).orElse(null);
        if (classMaster == null) {
            errors.add(MessageConstants.CLASS_NOT_FOUND);
            return;
        }

        if (!academicYearId.equals(classMaster.getAcademicYear().getId())) {
            errors.add(MessageConstants.CLASS_ACADEMIC_YEAR_MISMATCH);
        }

        SectionMaster section = sectionMasterRepository.findByIdAndDeletedFalse(sectionId).orElse(null);
        if (section == null) {
            errors.add(MessageConstants.SECTION_NOT_FOUND);
        } else if (!Boolean.TRUE.equals(section.getActive())) {
            errors.add(MessageConstants.SECTION_NOT_ACTIVE);
        }
    }
}
