package com.infiniteVision.schoolProject.modules.notification.service.impl;

import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.notification.entity.Notification;
import com.infiniteVision.schoolProject.modules.notification.enums.NotificationStatus;
import com.infiniteVision.schoolProject.modules.notification.enums.NotificationType;
import com.infiniteVision.schoolProject.modules.notification.repository.NotificationRepository;
import com.infiniteVision.schoolProject.modules.notification.service.ScholarshipNotificationDispatcher;
import com.infiniteVision.schoolProject.modules.scholarship.entity.StudentScholarshipApplication;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApprovalAction;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates UNREAD notifications for all active Principal and Correspondent users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScholarshipNotificationDispatcherImpl implements ScholarshipNotificationDispatcher {

    private static final String REFERENCE_TYPE = "SCHOLARSHIP_APPLICATION";

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyScholarshipEvent(
            StudentScholarshipApplication application,
            ScholarshipApprovalAction action,
            String submittedByUsername) {
        try {
            NotificationType type = mapType(action);
            String title = buildTitle(action);
            String body = buildBody(application, action, submittedByUsername);
            String payloadJson = serializePayload(application, action, submittedByUsername);

            List<User> recipients = userRepository.findAllByRoleInAndDeletedFalseAndStatusActive(
                    List.of(UserRole.PRINCIPAL, UserRole.CORRESPONDENT),
                    com.infiniteVision.schoolProject.modules.auth.enums.UserStatus.ACTIVE);

            for (User recipient : recipients) {
                Notification notification = Notification.builder()
                        .notificationType(type)
                        .recipientUserId(recipient.getId())
                        .title(title)
                        .body(body)
                        .status(NotificationStatus.UNREAD)
                        .referenceType(REFERENCE_TYPE)
                        .referenceId(application.getId())
                        .payloadJson(payloadJson)
                        .createdAt(LocalDateTime.now())
                        .build();
                notificationRepository.save(notification);
            }
            log.info(
                    "Scholarship notifications sent action={} applicationId={} recipients={}",
                    action,
                    application.getId(),
                    recipients.size());
        } catch (RuntimeException exception) {
            log.error(
                    "Failed to dispatch scholarship notifications applicationId={}",
                    application.getId(),
                    exception);
        }
    }

    private NotificationType mapType(ScholarshipApprovalAction action) {
        return switch (action) {
            case SUBMITTED -> NotificationType.SCHOLARSHIP_SUBMITTED;
            case APPROVED -> NotificationType.SCHOLARSHIP_APPROVED;
            case REJECTED -> NotificationType.SCHOLARSHIP_REJECTED;
        };
    }

    private String buildTitle(ScholarshipApprovalAction action) {
        return switch (action) {
            case SUBMITTED -> "Scholarship request submitted";
            case APPROVED -> "Scholarship request approved";
            case REJECTED -> "Scholarship request rejected";
        };
    }

    private String buildBody(
            StudentScholarshipApplication application,
            ScholarshipApprovalAction action,
            String submittedByUsername) {
        Student student = application.getStudent();
        String studentName = student != null ? student.getFirstName() : "Student";
        String schemeName =
                application.getScheme() != null ? application.getScheme().getSchemeName() : "Scholarship";
        return switch (action) {
            case SUBMITTED -> studentName + " — " + schemeName + " (submitted by " + submittedByUsername + ")";
            case APPROVED -> studentName + " — " + schemeName + " approved";
            case REJECTED -> studentName + " — " + schemeName + " rejected";
        };
    }

    private String serializePayload(
            StudentScholarshipApplication application,
            ScholarshipApprovalAction action,
            String submittedByUsername) {
        Student student = application.getStudent();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("action", action.name());
        payload.put("applicationId", application.getId());
        payload.put("studentId", student != null ? student.getId() : null);
        payload.put("studentName", student != null ? student.getFirstName() : null);
        payload.put("classId", student != null ? student.getClassId() : null);
        payload.put("schemeName", application.getScheme() != null ? application.getScheme().getSchemeName() : null);
        payload.put("status", application.getStatus() != null ? application.getStatus().name() : null);
        payload.put("requestedDiscountPercent", application.getRequestedDiscountPercent());
        payload.put("approvedDiscountPercent", application.getApprovedDiscountPercent());
        payload.put("submittedBy", submittedByUsername);
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (tools.jackson.core.JacksonException exception) {
            log.warn("Notification payload serialization failed", exception);
            return null;
        }
    }
}
