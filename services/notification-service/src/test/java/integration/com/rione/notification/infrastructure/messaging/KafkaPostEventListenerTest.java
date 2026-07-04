package com.rione.notification.infrastructure.messaging;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.domain.NotificationType;

import tools.jackson.databind.ObjectMapper;

class KafkaPostEventListenerTest {

	@Test
	void mapsPostCommentEventToPostNotification() {
		NotificationService service = org.mockito.Mockito.mock(NotificationService.class);
		KafkaPostEventListener listener = new KafkaPostEventListener(service, new ObjectMapper());

		listener.listen("""
				{"type":"POST_COMMENT_ADDED","postId":10,"recipientId":1,"actorId":2,"occurredAt":"2026-01-01T10:00:00"}
				""");

		ArgumentCaptor<NotificationService.PostEventCommand> captor = ArgumentCaptor
			.forClass(NotificationService.PostEventCommand.class);
		verify(service).recordPostEvent(captor.capture());
		org.assertj.core.api.Assertions.assertThat(captor.getValue())
			.extracting(NotificationService.PostEventCommand::type, NotificationService.PostEventCommand::recipientId,
					NotificationService.PostEventCommand::actorId, NotificationService.PostEventCommand::postId)
			.containsExactly(NotificationType.POST_COMMENT_ADDED, 1L, 2L, 10L);
	}

	@Test
	void mapsPostReactionEventToPostNotification() {
		NotificationService service = org.mockito.Mockito.mock(NotificationService.class);
		KafkaPostEventListener listener = new KafkaPostEventListener(service, new ObjectMapper());

		listener.listen("""
				{"type":"POST_REACTION_ADDED","postId":10,"recipientId":1,"actorId":2,"reactionType":"UPVOTE","occurredAt":"2026-01-01T10:00:00"}
				""");

		ArgumentCaptor<NotificationService.PostEventCommand> captor = ArgumentCaptor
			.forClass(NotificationService.PostEventCommand.class);
		verify(service).recordPostEvent(captor.capture());
		org.assertj.core.api.Assertions.assertThat(captor.getValue().type()).isEqualTo(NotificationType.POST_REACTION_ADDED);
	}
}
