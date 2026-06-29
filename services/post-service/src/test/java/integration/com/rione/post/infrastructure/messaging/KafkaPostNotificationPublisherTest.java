package com.rione.post.infrastructure.messaging;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import com.rione.post.domain.event.CommentAdded;
import com.rione.post.domain.event.ReactionAdded;
import com.rione.post.domain.model.CommentContent;
import com.rione.post.domain.model.CommentId;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.ReactionId;
import com.rione.post.domain.model.ReactionType;
import com.rione.post.domain.model.UserId;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class KafkaPostNotificationPublisherTest {

	@Test
	void publishesCommentAddedEventForPostAuthor() throws Exception {
		@SuppressWarnings("unchecked")
		KafkaTemplate<String, String> kafkaTemplate = org.mockito.Mockito.mock(KafkaTemplate.class);
		ObjectMapper objectMapper = new ObjectMapper();
		KafkaPostNotificationPublisher publisher = new KafkaPostNotificationPublisher(kafkaTemplate, objectMapper, "post");

		publisher.publishCommentAdded(new CommentAdded(new PostId(1L), new CommentId(2L), new UserId(3L),
				new CommentContent("This comment is long enough"), LocalDateTime.of(2026, 1, 1, 10, 0)),
				new UserId(4L));

		ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
		verify(kafkaTemplate).send(eq("post"), eq("4"), payload.capture());
		JsonNode json = objectMapper.readTree(payload.getValue());
		org.assertj.core.api.Assertions.assertThat(json.get("type").asText()).isEqualTo("POST_COMMENT_ADDED");
		org.assertj.core.api.Assertions.assertThat(json.get("recipientId").asLong()).isEqualTo(4L);
		org.assertj.core.api.Assertions.assertThat(json.get("actorId").asLong()).isEqualTo(3L);
	}

	@Test
	void publishesReactionAddedEventForPostAuthor() throws Exception {
		@SuppressWarnings("unchecked")
		KafkaTemplate<String, String> kafkaTemplate = org.mockito.Mockito.mock(KafkaTemplate.class);
		ObjectMapper objectMapper = new ObjectMapper();
		KafkaPostNotificationPublisher publisher = new KafkaPostNotificationPublisher(kafkaTemplate, objectMapper, "post");

		publisher.publishReactionAdded(new ReactionAdded(new PostId(1L), new ReactionId(2L), new UserId(3L),
				ReactionType.UPVOTE, LocalDateTime.of(2026, 1, 1, 10, 0)), new UserId(4L));

		ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
		verify(kafkaTemplate).send(eq("post"), eq("4"), payload.capture());
		JsonNode json = objectMapper.readTree(payload.getValue());
		org.assertj.core.api.Assertions.assertThat(json.get("type").asText()).isEqualTo("POST_REACTION_ADDED");
		org.assertj.core.api.Assertions.assertThat(json.get("reactionType").asText()).isEqualTo("UPVOTE");
	}
}
