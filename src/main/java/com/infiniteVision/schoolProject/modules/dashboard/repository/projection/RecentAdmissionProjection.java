package com.infiniteVision.schoolProject.modules.dashboard.repository.projection;

import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;

/**
 * Lightweight student row for dashboard recent admissions.
 */
public interface RecentAdmissionProjection {

    String getApplicationNo();

    String getStudentName();

    String getClassName();

    StudentStatus getStatus();
}
