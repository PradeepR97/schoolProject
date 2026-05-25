package com.infiniteVision.schoolProject.modules.notification.dto.response;

import com.infiniteVision.schoolProject.modules.notification.enums.NotificationStatus;
import com.infiniteVision.schoolProject.modules.notification.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * In-app notification row for API responses.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {

    private Long notificationId;
    private NotificationType notificationType;
    private String title;
    private String body;
    private NotificationStatus status;
    private String referenceType;
    private Long referenceId;
    private String payloadJson;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
