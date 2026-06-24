package com.rione.notification.infrastructure.persistence;

import java.time.LocalDateTime;

import com.rione.notification.domain.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
class NotificationJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long recipientId;

	@Column(nullable = false)
	private Long actorId;

	@Column(nullable = false)
	private Long requestId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private NotificationType type;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	private LocalDateTime occurredAt;

	private LocalDateTime readAt;

	protected NotificationJpaEntity() {
	}

	NotificationJpaEntity(Long id, Long recipientId, Long actorId, Long requestId, NotificationType type,
			String title, String message, LocalDateTime occurredAt, LocalDateTime readAt) {
		this.id = id;
		this.recipientId = recipientId;
		this.actorId = actorId;
		this.requestId = requestId;
		this.type = type;
		this.title = title;
		this.message = message;
		this.occurredAt = occurredAt;
		this.readAt = readAt;
	}

	Long id() {
		return id;
	}

	Long recipientId() {
		return recipientId;
	}

	Long actorId() {
		return actorId;
	}

	Long requestId() {
		return requestId;
	}

	NotificationType type() {
		return type;
	}

	String title() {
		return title;
	}

	String message() {
		return message;
	}

	LocalDateTime occurredAt() {
		return occurredAt;
	}

	LocalDateTime readAt() {
		return readAt;
	}
}
