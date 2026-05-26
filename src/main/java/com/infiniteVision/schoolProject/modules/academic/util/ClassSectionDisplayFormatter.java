package com.infiniteVision.schoolProject.modules.academic.util;

import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;

/**
 * Builds display labels for grade + section (e.g. {@code Class 10 - A}, {@code LKG - B}).
 */
public final class ClassSectionDisplayFormatter {

    private ClassSectionDisplayFormatter() {}

    public static String format(ClassMaster classMaster, SectionMaster section) {
        if (classMaster == null) {
            return null;
        }
        String gradeLabel = formatGradeOnly(classMaster.getClassName());
        if (section == null || section.getSectionCode() == null || section.getSectionCode().isBlank()) {
            return gradeLabel;
        }
        return gradeLabel + " - " + section.getSectionCode();
    }

    public static String formatGradeOnly(String className) {
        if (className == null || className.isBlank()) {
            return "";
        }
        if (isPrePrimary(className)) {
            return className.trim();
        }
        return "Class " + className.trim();
    }

    private static boolean isPrePrimary(String className) {
        return "LKG".equalsIgnoreCase(className.trim()) || "UKG".equalsIgnoreCase(className.trim());
    }
}
