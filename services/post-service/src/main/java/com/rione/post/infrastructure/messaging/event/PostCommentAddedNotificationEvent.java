package com.rione.post.infrastructure.messaging.event;

import java.time.LocalDateTime;

public record PostCommentAddedNotificationEvent(PostNotificationEventType type, Long postId, Long commentId,
		Long recipientId, Long actorId, LocalDateTime occurredAt) {

	public static PostCommentAddedNotificationEvent create(Long postId, Long commentId, Long recipientId, Long actorId,
			LocalDateTime occurredAt) {
		return new PostCommentAddedNotificationEvent(PostNotificationEventType.POST_COMMENT_ADDED, postId, commentId,
				recipientId, actorId, occurredAt);
	}
}
