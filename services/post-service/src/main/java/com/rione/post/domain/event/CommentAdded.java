package com.rione.post.domain.event;

import java.time.LocalDateTime;

import com.rione.post.domain.model.CommentContent;
import com.rione.post.domain.model.CommentId;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.UserId;

public record CommentAdded(PostId postId, CommentId commentId, UserId author, CommentContent content,
		LocalDateTime occurredAt) implements PostEvent {

	@Override
	public PostEventType type() {
		return PostEventType.COMMENT_ADDED;
	}
}
