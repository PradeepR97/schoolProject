package com.infiniteVision.schoolProject.modules.notification.service;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.modules.notification.dto.response.NotificationResponseDTO;
import com.infiniteVision.schoolProject.modules.notification.enums.NotificationStatus;

/**
 * In-app notifications for staff users.
 */
public interface NotificationService {

    PagedResponseDTO<NotificationResponseDTO> listForCurrentUser(NotificationStatus status, int page, int size);

    long countUnreadForCurrentUser();

    NotificationResponseDTO markAsRead(Long notificationId);
}
