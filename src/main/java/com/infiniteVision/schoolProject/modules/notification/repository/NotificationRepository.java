package com.infiniteVision.schoolProject.modules.notification.repository;

import com.infiniteVision.schoolProject.modules.notification.entity.Notification;
import com.infiniteVision.schoolProject.modules.notification.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * In-app notification persistence.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId, Pageable pageable);

    Page<Notification> findAllByRecipientUserIdAndStatusOrderByCreatedAtDesc(
            Long recipientUserId, NotificationStatus status, Pageable pageable);

    long countByRecipientUserIdAndStatus(Long recipientUserId, NotificationStatus status);
}
