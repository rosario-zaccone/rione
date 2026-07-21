package com.rione.notification.infrastructure.messaging;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.application.port.out.SocialEventListener;
import com.rione.notification.domain.NotificationType;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import tools.jackson.databind.ObjectMapper;

@Component
class KafkaSocialEventListener implements SocialEventListener {

	private final NotificationService notificationService;
	private final ObjectMapper objectMapper;
	private final Clock clock;
	private final Timer deliveryLatencyTimer;

	KafkaSocialEventListener(NotificationService notificationService, ObjectMapper objectMapper, Clock clock,
			MeterRegistry meterRegistry) {
		this.notificationService = notificationService;
		this.objectMapper = objectMapper;
		this.clock = clock;
		this.deliveryLatencyTimer = Timer.builder("rione_social_event_delivery_seconds")
			.description("Time between a social event being published and consumed by notification-service")
			.serviceLevelObjectives(Duration.ofSeconds(2))
			.register(meterRegistry);
	}

	@Override
	@KafkaListener(topics = "${rione.messaging.social-topic:social}")
	public void listen(String payload) {
		NeighborRequestEvent event = read(payload);
		recordDeliveryLatency(event);
		NotificationType type = NotificationType.valueOf(event.type());
		Long recipientId = type == NotificationType.REQUEST_RECEIVED ? event.receiverId() : event.senderId();
		Long actorId = type == NotificationType.REQUEST_RECEIVED ? event.senderId() : event.receiverId();
		notificationService.recordNeighborRequestEvent(new NotificationService.NeighborRequestEventCommand(type,
				recipientId, actorId, event.requestId(), event.occurredAt()));
	}

	private void recordDeliveryLatency(NeighborRequestEvent event) {
		Duration latency = Duration.between(event.occurredAt(), LocalDateTime.now(clock));
		deliveryLatencyTimer.record(latency.isNegative() ? Duration.ZERO : latency);
	}

	private NeighborRequestEvent read(String payload) {
		try {
			return objectMapper.readValue(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8),
					NeighborRequestEvent.class);
		}
		catch (Exception exception) {
			throw new IllegalArgumentException("Invalid social event payload", exception);
		}
	}

	record NeighborRequestEvent(String type, Long requestId, Long senderId, Long receiverId, LocalDateTime occurredAt) {
	}
}
