package com.infiniteVision.schoolProject.modules.notification.service;

import com.infiniteVision.schoolProject.modules.scholarship.entity.StudentScholarshipApplication;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApprovalAction;

/**
 * Sends in-app notifications to Principal and Correspondent for scholarship workflow events.
 */
public interface ScholarshipNotificationDispatcher {

    void notifyScholarshipEvent(
            StudentScholarshipApplication application,
            ScholarshipApprovalAction action,
            String submittedByUsername);
}
