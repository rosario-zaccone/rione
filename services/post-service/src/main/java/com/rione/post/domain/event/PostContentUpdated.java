package com.rione.post.domain.event;

import java.time.LocalDateTime;

import com.rione.post.domain.model.PostContent;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.UserId;

public record PostContentUpdated(PostId postId, UserId author, PostContent content, LocalDateTime occurredAt)
		implements PostEvent {

	@Override
	public PostEventType type() {
		return PostEventType.POST_CONTENT_UPDATED;
	}
}
