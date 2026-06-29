package com.rione.notification.infrastructure.messaging;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.domain.NotificationType;

import tools.jackson.databind.ObjectMapper;

@Component
class PostEventListener {

	private final NotificationService notificationService;
	private final ObjectMapper objectMapper;

	PostEventListener(NotificationService notificationService, ObjectMapper objectMapper) {
		this.notificationService = notificationService;
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = "${rione.messaging.post-topic:post}")
	void listen(String payload) {
		PostEvent event = read(payload);
		notificationService.recordPostEvent(new NotificationService.PostEventCommand(NotificationType.valueOf(event.type()),
				event.recipientId(), event.actorId(), event.postId(), event.occurredAt()));
	}

	private PostEvent read(String payload) {
		try {
			return objectMapper.readValue(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8), PostEvent.class);
		}
		catch (Exception exception) {
			throw new IllegalArgumentException("Invalid post event payload", exception);
		}
	}

	record PostEvent(String type, Long postId, Long recipientId, Long actorId, LocalDateTime occurredAt) {
	}
}
