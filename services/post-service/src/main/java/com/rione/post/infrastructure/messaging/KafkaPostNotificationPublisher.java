package com.rione.post.infrastructure.messaging;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rione.post.application.port.out.PostNotificationPublisher;
import com.rione.post.domain.event.CommentAdded;
import com.rione.post.domain.event.ReactionAdded;
import com.rione.post.domain.model.UserId;
import com.rione.post.infrastructure.messaging.event.PostCommentAddedNotificationEvent;
import com.rione.post.infrastructure.messaging.event.PostReactionAddedNotificationEvent;

import tools.jackson.databind.ObjectMapper;

@Component
class KafkaPostNotificationPublisher implements PostNotificationPublisher {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final String topic;

	KafkaPostNotificationPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
			@Value("${rione.messaging.post-topic:post}") String topic) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.topic = topic;
	}

	@Override
	public void publishCommentAdded(CommentAdded event, UserId postAuthor) {
		if (postAuthor.equals(event.author())) {
			return;
		}
		publish(postAuthor.value().toString(), PostCommentAddedNotificationEvent.create(event.postId().value(),
				event.commentId().value(), postAuthor.value(), event.author().value(), LocalDateTime.now()));
	}

	@Override
	public void publishReactionAdded(ReactionAdded event, UserId postAuthor) {
		if (postAuthor.equals(event.author())) {
			return;
		}
		publish(postAuthor.value().toString(),
				PostReactionAddedNotificationEvent.create(event.postId().value(), event.reactionId().value(),
						postAuthor.value(), event.author().value(), event.reactionType(), LocalDateTime.now()));
	}

	private void publish(String key, Object event) {
		try {
			String payload = new String(objectMapper.writeValueAsBytes(event), java.nio.charset.StandardCharsets.UTF_8);
			kafkaTemplate.send(topic, key, payload);
		}
		catch (Exception exception) {
			throw new IllegalStateException("Unable to serialize post notification event", exception);
		}
	}
}
