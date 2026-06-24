package com.rione.notification.infrastructure.messaging;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.domain.NotificationType;

class SocialEventListenerTest {

	@Test
	void mapsRequestReceivedEventToReceiverNotification() {
		NotificationService service = org.mockito.Mockito.mock(NotificationService.class);
		SocialEventListener listener = new SocialEventListener(service, new ObjectMapper().findAndRegisterModules());

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
		SocialEventListener listener = new SocialEventListener(service, new ObjectMapper().findAndRegisterModules());

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
}
