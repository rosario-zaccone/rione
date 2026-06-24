package com.rione.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.application.port.out.NotificationRepository;
import com.rione.notification.application.service.NotificationServiceImpl;
import com.rione.notification.domain.Notification;
import com.rione.notification.domain.NotificationId;
import com.rione.notification.domain.NotificationType;
import com.rione.notification.domain.UserId;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class NotificationStepDefinitions {

	private final AtomicLong sequence = new AtomicLong();
	private final List<Notification> saved = new ArrayList<>();
	private NotificationService service;
	private NotificationService.NotificationResponse response;

	@Before
	public void reset() {
		sequence.set(0);
		saved.clear();
		service = new NotificationServiceImpl(new FakeNotificationRepository());
	}

	@When("a request received event says user {long} sent request {long} to user {long}")
	public void aRequestReceivedEventSaysUserSentRequestToUser(Long senderId, Long requestId, Long receiverId) {
		response = service.recordNeighborRequestEvent(new NotificationService.NeighborRequestEventCommand(
				NotificationType.REQUEST_RECEIVED, receiverId, senderId, requestId, LocalDateTime.now()));
	}

	@When("a request accepted event says user {long} accepted request {long} from user {long}")
	public void aRequestAcceptedEventSaysUserAcceptedRequestFromUser(Long receiverId, Long requestId, Long senderId) {
		response = service.recordNeighborRequestEvent(new NotificationService.NeighborRequestEventCommand(
				NotificationType.REQUEST_ACCEPTED, senderId, receiverId, requestId, LocalDateTime.now()));
	}

	@Then("user {long} has a {string} notification from user {long}")
	public void userHasANotificationFromUser(Long recipientId, String type, Long actorId) {
		assertThat(response.recipientId()).isEqualTo(recipientId);
		assertThat(response.actorId()).isEqualTo(actorId);
		assertThat(response.type()).isEqualTo(type);
	}

	private class FakeNotificationRepository implements NotificationRepository {

		@Override
		public Notification save(Notification notification) {
			Notification savedNotification = notification.id() == null
					? Notification.restore(new NotificationId(sequence.incrementAndGet()), notification.recipient(),
							notification.actor(), notification.requestId(), notification.type(), notification.title(),
							notification.message(), notification.occurredAt(), notification.readAt())
					: notification;
			saved.add(savedNotification);
			return savedNotification;
		}

		@Override
		public Optional<Notification> findById(NotificationId id) {
			return saved.stream().filter(notification -> notification.id().equals(id)).findFirst();
		}

		@Override
		public List<Notification> findByRecipient(UserId recipient) {
			return saved.stream().filter(notification -> notification.recipient().equals(recipient)).toList();
		}
	}
}
