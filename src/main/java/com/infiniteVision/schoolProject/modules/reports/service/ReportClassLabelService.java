package com.infiniteVision.schoolProject.modules.reports.service;

import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.util.ClassSectionDisplayFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves grade display labels for report rows and filters.
 */
@Service
@RequiredArgsConstructor
public class ReportClassLabelService {

    private final ClassMasterRepository classMasterRepository;

    public Map<Long, String> loadClassNamesByAcademicYear(Long academicYearId) {
        List<ClassMaster> classes =
                classMasterRepository.findAllWithAcademicYearByAcademicYearIdAndDeletedFalseOrderByClassNameAsc(
                        academicYearId);
        Map<Long, String> labels = new HashMap<>();
        for (ClassMaster classMaster : classes) {
            labels.put(classMaster.getId(), ClassSectionDisplayFormatter.formatGradeOnly(classMaster.getClassName()));
        }
        return labels;
    }

    public String resolveClassName(Map<Long, String> classNamesById, Long classId) {
        if (classId == null) {
            return "";
        }
        return classNamesById.getOrDefault(classId, "Class " + classId);
    }

    /** Label for report title when filtering by a single grade. */
    public String resolveClassFilterTitle(Long academicYearId, Long classId) {
        if (classId == null) {
            return "All Classes";
        }
        Map<Long, String> classNames = loadClassNamesByAcademicYear(academicYearId);
        return resolveClassName(classNames, classId);
    }
}
