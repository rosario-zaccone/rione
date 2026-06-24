package com.rione.notification.infrastructure.messaging;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.domain.NotificationType;

@Component
class SocialEventListener {

	private final NotificationService notificationService;
	private final ObjectMapper objectMapper;

	SocialEventListener(NotificationService notificationService, ObjectMapper objectMapper) {
		this.notificationService = notificationService;
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = "${rione.messaging.social-topic:social}")
	void listen(String payload) {
		NeighborRequestEvent event = read(payload);
		NotificationType type = NotificationType.valueOf(event.type());
		Long recipientId = type == NotificationType.REQUEST_RECEIVED ? event.receiverId() : event.senderId();
		Long actorId = type == NotificationType.REQUEST_RECEIVED ? event.senderId() : event.receiverId();
		notificationService.recordNeighborRequestEvent(new NotificationService.NeighborRequestEventCommand(type,
				recipientId, actorId, event.requestId(), event.occurredAt()));
	}

	private NeighborRequestEvent read(String payload) {
		try {
			return objectMapper.readValue(payload, NeighborRequestEvent.class);
		}
		catch (JsonProcessingException exception) {
			throw new IllegalArgumentException("Invalid social event payload", exception);
		}
	}

	record NeighborRequestEvent(String type, Long requestId, Long senderId, Long receiverId, LocalDateTime occurredAt) {
	}
}
