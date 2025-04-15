package com.smartpos.notification.repository;

import com.smartpos.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStatusOrderByCreatedAtAsc(String status);
    List<Notification> findByTypeAndStatusOrderByCreatedAtAsc(String type, String status);
    List<Notification> findByRecipientAndCreatedAtAfter(String recipient, LocalDateTime dateTime);
}
