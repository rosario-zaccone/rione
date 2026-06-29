package com.rione.post.domain.event;

import java.time.LocalDateTime;

import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.UserId;

public record PostDeleted(PostId postId, UserId author, LocalDateTime occurredAt) implements PostEvent {

	@Override
	public PostEventType type() {
		return PostEventType.POST_DELETED;
	}
}
