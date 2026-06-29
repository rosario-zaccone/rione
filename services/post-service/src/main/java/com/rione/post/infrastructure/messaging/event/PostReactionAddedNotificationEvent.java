package com.rione.post.infrastructure.messaging.event;

import java.time.LocalDateTime;

import com.rione.post.domain.model.ReactionType;

public record PostReactionAddedNotificationEvent(PostNotificationEventType type, Long postId, Long reactionId,
		Long recipientId, Long actorId, ReactionType reactionType, LocalDateTime occurredAt) {

	public static PostReactionAddedNotificationEvent create(Long postId, Long reactionId, Long recipientId, Long actorId,
			ReactionType reactionType, LocalDateTime occurredAt) {
		return new PostReactionAddedNotificationEvent(PostNotificationEventType.POST_REACTION_ADDED, postId,
				reactionId, recipientId, actorId, reactionType, occurredAt);
	}
}
