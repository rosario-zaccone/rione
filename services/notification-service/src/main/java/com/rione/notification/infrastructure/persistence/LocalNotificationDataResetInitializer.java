package com.rione.notification.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rione.notification.application.port.out.NotificationRepository;
import com.rione.notification.domain.Notification;
import com.rione.notification.domain.UserId;

import jakarta.persistence.EntityManager;

@Component
@Profile("local")
class LocalNotificationDataResetInitializer implements ApplicationRunner {

	private final EntityManager entityManager;
	private final NotificationRepository notifications;

	LocalNotificationDataResetInitializer(EntityManager entityManager, NotificationRepository notifications) {
		this.entityManager = entityManager;
		this.notifications = notifications;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		entityManager.createNativeQuery("TRUNCATE TABLE notifications RESTART IDENTITY").executeUpdate();

		for (SeedNotification seed : seedNotifications()) {
			Notification notification = seed.factory().apply(seed);
			if (seed.read()) {
				notification.markRead(seed.occurredAt().plusHours(1));
			}
			notifications.save(notification);
		}
	}

	private static List<SeedNotification> seedNotifications() {
		return List.of(
				// pending neighbor requests received (mirrors social-service seed data)
				new SeedNotification(9L, 3L, 1L, LocalDateTime.of(2026, 6, 25, 10, 0), true, requestReceived()),
				new SeedNotification(10L, 7L, 2L, LocalDateTime.of(2026, 6, 26, 15, 30), false, requestReceived()),
				new SeedNotification(21L, 14L, 3L, LocalDateTime.of(2026, 6, 27, 8, 15), false, requestReceived()),
				new SeedNotification(24L, 18L, 4L, LocalDateTime.of(2026, 6, 28, 17, 45), false, requestReceived()),
				// accepted neighbor requests (mirrors accepted neighborships in social-service)
				new SeedNotification(1L, 2L, 5L, LocalDateTime.of(2026, 5, 2, 10, 5), true, requestAccepted()),
				new SeedNotification(11L, 12L, 6L, LocalDateTime.of(2026, 5, 10, 10, 5), false, requestAccepted()),
				// comments on posts (mirrors post-service seed data)
				new SeedNotification(1L, 4L, 1L, LocalDateTime.of(2026, 6, 20, 9, 0), false, postCommentAdded()),
				new SeedNotification(3L, 9L, 2L, LocalDateTime.of(2026, 6, 21, 19, 0), false, postCommentAdded()),
				new SeedNotification(6L, 2L, 3L, LocalDateTime.of(2026, 6, 22, 9, 30), false, postCommentAdded()),
				new SeedNotification(11L, 19L, 4L, LocalDateTime.of(2026, 6, 20, 17, 10), false, postCommentAdded()),
				new SeedNotification(14L, 16L, 5L, LocalDateTime.of(2026, 6, 23, 13, 0), false, postCommentAdded()),
				new SeedNotification(19L, 12L, 6L, LocalDateTime.of(2026, 6, 24, 19, 40), false, postCommentAdded()),
				// reactions on posts (mirrors post-service seed data)
				new SeedNotification(1L, 6L, 1L, LocalDateTime.of(2026, 6, 20, 9, 15), true, postReactionAdded()),
				new SeedNotification(3L, 1L, 2L, LocalDateTime.of(2026, 6, 21, 19, 5), false, postReactionAdded()),
				new SeedNotification(6L, 8L, 3L, LocalDateTime.of(2026, 6, 22, 9, 45), false, postReactionAdded()),
				new SeedNotification(11L, 14L, 4L, LocalDateTime.of(2026, 6, 20, 17, 20), false, postReactionAdded()),
				new SeedNotification(14L, 18L, 5L, LocalDateTime.of(2026, 6, 23, 13, 5), false, postReactionAdded()),
				new SeedNotification(19L, 11L, 6L, LocalDateTime.of(2026, 6, 24, 19, 45), false, postReactionAdded()));
	}

	private static Function<SeedNotification, Notification> requestReceived() {
		return seed -> Notification.requestReceived(new UserId(seed.recipientId()), new UserId(seed.actorId()),
				seed.resourceId(), seed.occurredAt());
	}

	private static Function<SeedNotification, Notification> requestAccepted() {
		return seed -> Notification.requestAccepted(new UserId(seed.recipientId()), new UserId(seed.actorId()),
				seed.resourceId(), seed.occurredAt());
	}

	private static Function<SeedNotification, Notification> postCommentAdded() {
		return seed -> Notification.postCommentAdded(new UserId(seed.recipientId()), new UserId(seed.actorId()),
				seed.resourceId(), seed.occurredAt());
	}

	private static Function<SeedNotification, Notification> postReactionAdded() {
		return seed -> Notification.postReactionAdded(new UserId(seed.recipientId()), new UserId(seed.actorId()),
				seed.resourceId(), seed.occurredAt());
	}

	private record SeedNotification(Long recipientId, Long actorId, Long resourceId, LocalDateTime occurredAt,
			boolean read, Function<SeedNotification, Notification> factory) {
	}
}
