package com.rione.notification.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.domain.NotificationType;

import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import tools.jackson.databind.ObjectMapper;

class KafkaSocialEventListenerTest {

	private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC);

	@Test
	void mapsRequestReceivedEventToReceiverNotification() {
		NotificationService service = org.mockito.Mockito.mock(NotificationService.class);
		KafkaSocialEventListener listener = new KafkaSocialEventListener(service, new ObjectMapper(), FIXED_CLOCK,
				new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));

		listener.listen("""
				{"type":"REQUEST_RECEIVED","requestId":10,"senderId":1,"receiverId":2,"occurredAt":"2026-01-01T10:00:00"}
				""");

		ArgumentCaptor<NotificationService.NeighborRequestEventCommand> captor = ArgumentCaptor
			.forClass(NotificationService.NeighborRequestEventCommand.class);
		verify(service).recordNeighborRequestEvent(captor.capture());
		org.assertj.core.api.Assertions.assertThat(captor.getValue())
			.extracting(NotificationService.NeighborRequestEventCommand::type,
					NotificationService.NeighborRequestEventCommand::recipientId,
					NotificationService.NeighborRequestEventCommand::actorId,
					NotificationService.NeighborRequestEventCommand::requestId)
			.containsExactly(NotificationType.REQUEST_RECEIVED, 2L, 1L, 10L);
	}

	@Test
	void mapsRequestAcceptedEventToSenderNotification() {
		NotificationService service = org.mockito.Mockito.mock(NotificationService.class);
		KafkaSocialEventListener listener = new KafkaSocialEventListener(service, new ObjectMapper(), FIXED_CLOCK,
				new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));

		listener.listen("""
				{"type":"REQUEST_ACCEPTED","requestId":10,"senderId":1,"receiverId":2,"occurredAt":"2026-01-01T10:00:00"}
				""");

		ArgumentCaptor<NotificationService.NeighborRequestEventCommand> captor = ArgumentCaptor
			.forClass(NotificationService.NeighborRequestEventCommand.class);
		verify(service).recordNeighborRequestEvent(captor.capture());
		org.assertj.core.api.Assertions.assertThat(captor.getValue())
			.extracting(NotificationService.NeighborRequestEventCommand::type,
					NotificationService.NeighborRequestEventCommand::recipientId,
					NotificationService.NeighborRequestEventCommand::actorId,
					NotificationService.NeighborRequestEventCommand::requestId)
			.containsExactly(NotificationType.REQUEST_ACCEPTED, 1L, 2L, 10L);
	}

	@Test
	void recordsDeliveryLatencyAgainstTheTwoSecondSlo() {
		NotificationService service = org.mockito.Mockito.mock(NotificationService.class);
		PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
		Clock clockHalfASecondLater = Clock.fixed(Instant.parse("2026-01-01T10:00:00.500Z"), ZoneOffset.UTC);
		KafkaSocialEventListener listener = new KafkaSocialEventListener(service, new ObjectMapper(),
				clockHalfASecondLater, registry);

		listener.listen("""
				{"type":"REQUEST_RECEIVED","requestId":10,"senderId":1,"receiverId":2,"occurredAt":"2026-01-01T10:00:00"}
				""");

		String scrape = registry.scrape();
		assertThat(scrape).contains("rione_social_event_delivery_seconds_count 1");
		assertThat(scrape).contains("rione_social_event_delivery_seconds_bucket{le=\"2.0\"} 1");
	}
}
