package com.rione.notification.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.application.port.out.NotificationRepository;
import com.rione.notification.domain.Notification;
import com.rione.notification.domain.NotificationId;
import com.rione.notification.domain.UserId;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notifications;

	public NotificationServiceImpl(NotificationRepository notifications) {
		this.notifications = notifications;
	}

	@Override
	public NotificationResponse recordNeighborRequestEvent(NeighborRequestEventCommand command) {
		UserId recipient = new UserId(command.recipientId());
		UserId actor = new UserId(command.actorId());
		Notification notification = switch (command.type()) {
			case REQUEST_RECEIVED -> Notification.requestReceived(recipient, actor, command.requestId(),
					command.occurredAt());
			case REQUEST_ACCEPTED -> Notification.requestAccepted(recipient, actor, command.requestId(),
					command.occurredAt());
		};
		return toResponse(notifications.save(notification));
	}

	@Override
	public List<NotificationResponse> getNotifications(Long recipientId) {
		return notifications.findByRecipient(new UserId(recipientId)).stream().map(this::toResponse).toList();
	}

	@Override
	public NotificationResponse markNotificationRead(MarkNotificationReadCommand command) {
		UserId recipient = new UserId(command.recipientId());
		Notification notification = notifications.findById(new NotificationId(command.notificationId()))
			.orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
		if (!notification.belongsTo(recipient)) {
			throw new NotificationNotFoundException("Notification not found");
		}
		notification.markRead(LocalDateTime.now());
		return toResponse(notifications.save(notification));
	}

	private NotificationResponse toResponse(Notification notification) {
		return new NotificationResponse(notification.id().value(), notification.recipient().value(),
				notification.actor().value(), notification.requestId(), notification.type().name(), notification.title(),
				notification.message(), notification.occurredAt(), notification.readAt());
	}
}
