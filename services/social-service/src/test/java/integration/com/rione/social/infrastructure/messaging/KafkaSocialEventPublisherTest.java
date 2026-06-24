package com.rione.social.infrastructure.messaging;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.NeighborRequestId;
import com.rione.social.domain.model.RequestStatus;
import com.rione.social.domain.model.UserId;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class KafkaSocialEventPublisherTest {

	@Test
	void publishesRequestReceivedEventAsJsonOnSocialTopic() throws Exception {
		@SuppressWarnings("unchecked")
		KafkaTemplate<String, String> kafkaTemplate = org.mockito.Mockito.mock(KafkaTemplate.class);
		ObjectMapper objectMapper = new ObjectMapper();
		KafkaSocialEventPublisher publisher = new KafkaSocialEventPublisher(kafkaTemplate, objectMapper, "social");
		NeighborRequest request = NeighborRequest.restore(new NeighborRequestId(10L), new UserId(1L),
				new UserId(2L), LocalDateTime.of(2026, 1, 1, 10, 0), RequestStatus.PENDING);

		publisher.publishNeighborRequestReceived(request);

		ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
		verify(kafkaTemplate).send(eq("social"), eq("2"), payload.capture());
		JsonNode json = objectMapper.readTree(payload.getValue());
		org.assertj.core.api.Assertions.assertThat(json.get("type").asText()).isEqualTo("REQUEST_RECEIVED");
		org.assertj.core.api.Assertions.assertThat(json.get("requestId").asLong()).isEqualTo(10L);
		org.assertj.core.api.Assertions.assertThat(json.get("senderId").asLong()).isEqualTo(1L);
		org.assertj.core.api.Assertions.assertThat(json.get("receiverId").asLong()).isEqualTo(2L);
	}

	@Test
	void usesOriginalSenderAsKeyForRequestAcceptedEvent() {
		@SuppressWarnings("unchecked")
		KafkaTemplate<String, String> kafkaTemplate = org.mockito.Mockito.mock(KafkaTemplate.class);
		KafkaSocialEventPublisher publisher = new KafkaSocialEventPublisher(kafkaTemplate, new ObjectMapper(), "social");
		NeighborRequest request = NeighborRequest.restore(new NeighborRequestId(10L), new UserId(1L),
				new UserId(2L), LocalDateTime.of(2026, 1, 1, 10, 0), RequestStatus.ACCEPTED);

		publisher.publishNeighborRequestAccepted(request);

		verify(kafkaTemplate).send(eq("social"), eq("1"), org.mockito.ArgumentMatchers.contains("REQUEST_ACCEPTED"));
	}
}
