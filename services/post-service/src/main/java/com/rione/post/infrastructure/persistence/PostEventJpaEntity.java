package com.rione.post.infrastructure.persistence;

import java.time.LocalDateTime;

import com.rione.post.domain.event.PostEventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "post_events", uniqueConstraints = @UniqueConstraint(columnNames = { "postId", "version" }),
		indexes = @Index(name = "idx_post_events_type_occurred_at", columnList = "type, occurredAt"))
class PostEventJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long postId;

	@Column(nullable = false)
	private long version;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private PostEventType type;

	@Column(nullable = false)
	private LocalDateTime occurredAt;

	@Column(nullable = false, columnDefinition = "text")
	private String payload;

	protected PostEventJpaEntity() {
	}

	PostEventJpaEntity(Long postId, long version, PostEventType type, LocalDateTime occurredAt, String payload) {
		this.postId = postId;
		this.version = version;
		this.type = type;
		this.occurredAt = occurredAt;
		this.payload = payload;
	}

	Long postId() {
		return postId;
	}

	long version() {
		return version;
	}

	PostEventType type() {
		return type;
	}

	LocalDateTime occurredAt() {
		return occurredAt;
	}

	String payload() {
		return payload;
	}
}
