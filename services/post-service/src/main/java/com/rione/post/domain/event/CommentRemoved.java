package com.rione.post.domain.event;

import java.time.LocalDateTime;

import com.rione.post.domain.model.CommentId;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.UserId;

public record CommentRemoved(PostId postId, CommentId commentId, UserId author, LocalDateTime occurredAt)
		implements PostEvent {

	@Override
	public PostEventType type() {
		return PostEventType.COMMENT_REMOVED;
	}
}
