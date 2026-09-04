package com.clausetrail.service;

import com.clausetrail.dto.NotificationResponse;
import com.clausetrail.model.Notification;
import com.clausetrail.model.NotificationType;
import com.clausetrail.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification notifyUser(
            String userId,
            String title,
            String message,
            NotificationType type,
            String contractId,
            String contractTitle
    ) {
        return notifyUser(userId, title, message, type, contractId, contractTitle, null);
    }

    public Notification notifyUser(
            String userId,
            String title,
            String message,
            NotificationType type,
            String contractId,
            String contractTitle,
            Integer versionNumber
    ) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .contractId(contractId)
                .contractTitle(contractTitle)
                .versionNumber(versionNumber)
                .read(false)
                .createdAt(Instant.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification sent to user '{}': '{}'", userId, title);
        return saved;
    }

    public List<NotificationResponse> getUserNotifications(String userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return notificationRepository.findByUserId(userId, sort).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public NotificationResponse markAsRead(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to access notification");
        }

        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    public void markAllAsRead(String userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId, sort);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    public NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .contractId(n.getContractId())
                .contractTitle(n.getContractTitle())
                .versionNumber(n.getVersionNumber())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
