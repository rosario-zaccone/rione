package com.rione.post.domain.event;

import java.time.LocalDateTime;

import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.ReactionId;
import com.rione.post.domain.model.UserId;

public record ReactionRemoved(PostId postId, ReactionId reactionId, UserId author, LocalDateTime occurredAt)
		implements PostEvent {

	@Override
	public PostEventType type() {
		return PostEventType.REACTION_REMOVED;
	}
}
