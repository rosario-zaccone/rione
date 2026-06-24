package com.rione.social.infrastructure.messaging;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rione.social.application.port.out.SocialEventPublisher;
import com.rione.social.domain.model.NeighborRequest;

@Component
class KafkaSocialEventPublisher implements SocialEventPublisher {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final String topic;

	KafkaSocialEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
			@Value("${rione.messaging.social-topic:social}") String topic) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.topic = topic;
	}

	@Override
	public void publishNeighborRequestReceived(NeighborRequest request) {
		publish("REQUEST_RECEIVED", request, request.receiver().value().toString());
	}

	@Override
	public void publishNeighborRequestAccepted(NeighborRequest request) {
		publish("REQUEST_ACCEPTED", request, request.sender().value().toString());
	}

	private void publish(String type, NeighborRequest request, String key) {
		try {
			kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(NeighborRequestEvent.from(type, request)));
		}
		catch (JsonProcessingException exception) {
			throw new IllegalStateException("Unable to serialize social event", exception);
		}
	}

	record NeighborRequestEvent(String type, Long requestId, Long senderId, Long receiverId, LocalDateTime occurredAt) {

		static NeighborRequestEvent from(String type, NeighborRequest request) {
			return new NeighborRequestEvent(type, request.id().value(), request.sender().value(),
					request.receiver().value(), LocalDateTime.now());
		}
	}
}
