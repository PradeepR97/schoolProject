package com.infiniteVision.schoolProject.modules.notification.service.impl;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.modules.notification.dto.response.NotificationResponseDTO;
import com.infiniteVision.schoolProject.modules.notification.entity.Notification;
import com.infiniteVision.schoolProject.modules.notification.enums.NotificationStatus;
import com.infiniteVision.schoolProject.modules.notification.repository.NotificationRepository;
import com.infiniteVision.schoolProject.modules.notification.service.NotificationService;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lists and marks read in-app notifications for the authenticated user.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<NotificationResponseDTO> listForCurrentUser(
            NotificationStatus status, int page, int size) {
        AuthenticatedUser user = currentUser();
        int effectiveSize = size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(page, effectiveSize);

        Page<Notification> notificationPage = status != null
                ? notificationRepository.findAllByRecipientUserIdAndStatusOrderByCreatedAtDesc(
                        user.getUserId(), status, pageable)
                : notificationRepository.findAllByRecipientUserIdOrderByCreatedAtDesc(
                        user.getUserId(), pageable);

        List<NotificationResponseDTO> content =
                notificationPage.getContent().stream().map(this::toResponse).toList();

        return PagedResponseDTO.<NotificationResponseDTO>builder()
                .content(content)
                .page(notificationPage.getNumber())
                .size(notificationPage.getSize())
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .first(notificationPage.isFirst())
                .last(notificationPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadForCurrentUser() {
        return notificationRepository.countByRecipientUserIdAndStatus(
                currentUser().getUserId(), NotificationStatus.UNREAD);
    }

    /**
     * Marks one notification as read when it belongs to the current user.
     */
    @Override
    @Transactional
    public NotificationResponseDTO markAsRead(Long notificationId) {
        AuthenticatedUser user = currentUser();
        Notification notification = notificationRepository
                .findById(notificationId)
                .filter(row -> user.getUserId().equals(row.getRecipientUserId()))
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.NOTIFICATION_NOT_FOUND));

        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        Notification saved = notificationRepository.save(notification);
        log.info("Notification marked read id={} userId={}", notificationId, user.getUserId());
        return toResponse(saved);
    }

    private NotificationResponseDTO toResponse(Notification notification) {
        return NotificationResponseDTO.builder()
                .notificationId(notification.getNotificationId())
                .notificationType(notification.getNotificationType())
                .title(notification.getTitle())
                .body(notification.getBody())
                .status(notification.getStatus())
                .referenceType(notification.getReferenceType())
                .referenceId(notification.getReferenceId())
                .payloadJson(notification.getPayloadJson())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
