package com.rione.notification.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import com.rione.common.application.InPort;
import com.rione.notification.domain.NotificationType;

@InPort
public interface NotificationService {

	NotificationResponse recordNeighborRequestEvent(NeighborRequestEventCommand command);

	NotificationResponse recordPostEvent(PostEventCommand command);

	List<NotificationResponse> getNotifications(Long recipientId);

	NotificationResponse markNotificationRead(MarkNotificationReadCommand command);

	record NeighborRequestEventCommand(NotificationType type, Long recipientId, Long actorId, Long requestId,
			LocalDateTime occurredAt) {
	}

	record PostEventCommand(NotificationType type, Long recipientId, Long actorId, Long postId,
			LocalDateTime occurredAt) {
	}

	record MarkNotificationReadCommand(Long recipientId, Long notificationId) {
	}

	record NotificationResponse(Long id, Long recipientId, Long actorId, Long requestId, Long postId, String type,
			String title, String message, LocalDateTime occurredAt, LocalDateTime readAt) {
	}
}
