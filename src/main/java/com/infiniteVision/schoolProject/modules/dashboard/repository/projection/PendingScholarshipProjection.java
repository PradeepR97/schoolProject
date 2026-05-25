package com.infiniteVision.schoolProject.modules.dashboard.repository.projection;

import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.math.BigDecimal;

/**
 * Scholarship application awaiting approval on the dashboard.
 */
public interface PendingScholarshipProjection {

    String getStudentName();

    String getSchemeName();

    BigDecimal getAmount();

    ScholarshipApplicationStatus getStatus();
}
