package com.infiniteVision.schoolProject.modules.reports.service;

import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.student.mapper.StudentListMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves class display labels for report rows.
 */
@Service
@RequiredArgsConstructor
public class ReportClassLabelService {

    private final ClassMasterRepository classMasterRepository;
    private final StudentListMapper studentListMapper;

    public Map<Long, String> loadClassNamesByAcademicYear(Long academicYearId) {
        List<ClassMaster> classes =
                classMasterRepository.findAllWithSectionByAcademicYearIdAndDeletedFalseOrderByClassNameAsc(
                        academicYearId);
        Map<Long, String> labels = new HashMap<>();
        for (ClassMaster classMaster : classes) {
            labels.put(classMaster.getId(), studentListMapper.formatClassName(classMaster));
        }
        return labels;
    }

    public String resolveClassName(Map<Long, String> classNamesById, Long classId) {
        if (classId == null) {
            return "";
        }
        return classNamesById.getOrDefault(classId, "Class " + classId);
    }

    /** Label for report title when filtering by a single class. */
    public String resolveClassFilterTitle(Long academicYearId, Long classId) {
        if (classId == null) {
            return "All Classes";
        }
        Map<Long, String> classNames = loadClassNamesByAcademicYear(academicYearId);
        return resolveClassName(classNames, classId);
    }
}
