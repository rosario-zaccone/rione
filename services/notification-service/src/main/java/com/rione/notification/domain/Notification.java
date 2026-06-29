package com.rione.notification.domain;

import java.time.LocalDateTime;

import com.rione.common.domain.DDDAggregateRoot;

@DDDAggregateRoot
public class Notification {

	private final NotificationId id;
	private final UserId recipient;
	private final UserId actor;
	private final Long requestId;
	private final NotificationType type;
	private final String title;
	private final String message;
	private final LocalDateTime occurredAt;
	private LocalDateTime readAt;

	private Notification(NotificationId id, UserId recipient, UserId actor, Long requestId, NotificationType type,
			String title, String message, LocalDateTime occurredAt, LocalDateTime readAt) {
		if (recipient.equals(actor)) {
			throw new DomainException("Notification recipient and actor must be different users");
		}
		if (requestId == null || requestId <= 0) {
			throw new DomainException("Notification resource id must be positive");
		}
		this.id = id;
		this.recipient = recipient;
		this.actor = actor;
		this.requestId = requestId;
		this.type = type;
		this.title = requireText(title, "Notification title");
		this.message = requireText(message, "Notification message");
		this.occurredAt = occurredAt == null ? LocalDateTime.now() : occurredAt;
		this.readAt = readAt;
	}

	public static Notification requestReceived(UserId receiver, UserId sender, Long requestId,
			LocalDateTime occurredAt) {
		return new Notification(null, receiver, sender, requestId, NotificationType.REQUEST_RECEIVED,
				"Neighbor request received", "User " + sender.value() + " sent you a neighbor request", occurredAt,
				null);
	}

	public static Notification requestAccepted(UserId sender, UserId receiver, Long requestId,
			LocalDateTime occurredAt) {
		return new Notification(null, sender, receiver, requestId, NotificationType.REQUEST_ACCEPTED,
				"Neighbor request accepted", "User " + receiver.value() + " accepted your neighbor request",
				occurredAt, null);
	}

	public static Notification postCommentAdded(UserId recipient, UserId actor, Long postId,
			LocalDateTime occurredAt) {
		return new Notification(null, recipient, actor, postId, NotificationType.POST_COMMENT_ADDED,
				"New comment on your post", "User " + actor.value() + " commented on your post", occurredAt, null);
	}

	public static Notification postReactionAdded(UserId recipient, UserId actor, Long postId,
			LocalDateTime occurredAt) {
		return new Notification(null, recipient, actor, postId, NotificationType.POST_REACTION_ADDED,
				"New reaction on your post", "User " + actor.value() + " reacted to your post", occurredAt, null);
	}

	public static Notification restore(NotificationId id, UserId recipient, UserId actor, Long requestId,
			NotificationType type, String title, String message, LocalDateTime occurredAt, LocalDateTime readAt) {
		return new Notification(id, recipient, actor, requestId, type, title, message, occurredAt, readAt);
	}

	public void markRead(LocalDateTime readAt) {
		if (this.readAt == null) {
			this.readAt = readAt == null ? LocalDateTime.now() : readAt;
		}
	}

	public boolean belongsTo(UserId userId) {
		return recipient.equals(userId);
	}

	private static String requireText(String value, String name) {
		if (value == null || value.isBlank()) {
			throw new DomainException(name + " is required");
		}
		return value.trim();
	}

	public NotificationId id() {
		return id;
	}

	public UserId recipient() {
		return recipient;
	}

	public UserId actor() {
		return actor;
	}

	public Long requestId() {
		return requestId;
	}

	public NotificationType type() {
		return type;
	}

	public String title() {
		return title;
	}

	public String message() {
		return message;
	}

	public LocalDateTime occurredAt() {
		return occurredAt;
	}

	public LocalDateTime readAt() {
		return readAt;
	}
}
