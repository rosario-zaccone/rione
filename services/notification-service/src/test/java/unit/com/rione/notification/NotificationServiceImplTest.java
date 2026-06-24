package com.rione.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.application.port.out.NotificationRepository;
import com.rione.notification.application.service.NotificationServiceImpl;
import com.rione.notification.domain.Notification;
import com.rione.notification.domain.NotificationId;
import com.rione.notification.domain.NotificationType;
import com.rione.notification.domain.UserId;

class NotificationServiceImplTest {

	private final AtomicLong sequence = new AtomicLong();
	private final List<Notification> saved = new ArrayList<>();
	private NotificationService service;

	@BeforeEach
	void setUp() {
		saved.clear();
		sequence.set(0);
		service = new NotificationServiceImpl(new FakeNotificationRepository());
	}

	@Test
	void requestReceivedEventCreatesNotificationForReceiver() {
		NotificationService.NotificationResponse response = service
			.recordNeighborRequestEvent(new NotificationService.NeighborRequestEventCommand(
					NotificationType.REQUEST_RECEIVED, 2L, 1L, 10L, LocalDateTime.of(2026, 1, 1, 10, 0)));

		assertThat(response)
			.extracting(NotificationService.NotificationResponse::recipientId,
					NotificationService.NotificationResponse::actorId,
					NotificationService.NotificationResponse::requestId, NotificationService.NotificationResponse::type)
			.containsExactly(2L, 1L, 10L, "REQUEST_RECEIVED");
	}

	@Test
	void requestAcceptedEventCreatesNotificationForSender() {
		NotificationService.NotificationResponse response = service
			.recordNeighborRequestEvent(new NotificationService.NeighborRequestEventCommand(
					NotificationType.REQUEST_ACCEPTED, 1L, 2L, 10L, LocalDateTime.of(2026, 1, 1, 10, 0)));

		assertThat(response)
			.extracting(NotificationService.NotificationResponse::recipientId,
					NotificationService.NotificationResponse::actorId,
					NotificationService.NotificationResponse::requestId, NotificationService.NotificationResponse::type)
			.containsExactly(1L, 2L, 10L, "REQUEST_ACCEPTED");
	}

	@Test
	void marksNotificationReadForRecipient() {
		NotificationService.NotificationResponse created = service
			.recordNeighborRequestEvent(new NotificationService.NeighborRequestEventCommand(
					NotificationType.REQUEST_RECEIVED, 2L, 1L, 10L, LocalDateTime.of(2026, 1, 1, 10, 0)));

		NotificationService.NotificationResponse read = service
			.markNotificationRead(new NotificationService.MarkNotificationReadCommand(2L, created.id()));

		assertThat(read.readAt()).isNotNull();
	}

	private class FakeNotificationRepository implements NotificationRepository {

		@Override
		public Notification save(Notification notification) {
			Notification savedNotification = notification.id() == null
					? Notification.restore(new NotificationId(sequence.incrementAndGet()), notification.recipient(),
							notification.actor(), notification.requestId(), notification.type(), notification.title(),
							notification.message(), notification.occurredAt(), notification.readAt())
					: notification;
			saved.removeIf(existing -> existing.id().equals(savedNotification.id()));
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
