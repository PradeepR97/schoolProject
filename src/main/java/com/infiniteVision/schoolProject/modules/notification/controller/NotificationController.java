package com.infiniteVision.schoolProject.modules.notification.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.modules.notification.constants.NotificationApiConstants;
import com.infiniteVision.schoolProject.modules.notification.dto.response.NotificationResponseDTO;
import com.infiniteVision.schoolProject.modules.notification.enums.NotificationStatus;
import com.infiniteVision.schoolProject.modules.notification.service.NotificationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * In-app notifications for staff (scholarship workflow and future channels).
 */
@RestController
@RequestMapping(value = NotificationApiConstants.NOTIFICATION_BASE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<PagedResponseDTO<NotificationResponseDTO>>> listNotifications(
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponseDTO<NotificationResponseDTO> data = notificationService.listForCurrentUser(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.NOTIFICATIONS_LISTED_SUCCESS, data));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount() {
        long count = notificationService.countUnreadForCurrentUser();
        return ResponseEntity.ok(
                ApiResponse.success(MessageConstants.NOTIFICATION_UNREAD_COUNT_SUCCESS, Map.of("count", count)));
    }

    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL', 'CORRESPONDENT', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> markAsRead(@PathVariable Long notificationId) {
        NotificationResponseDTO data = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.NOTIFICATION_READ_SUCCESS, data));
    }
}
