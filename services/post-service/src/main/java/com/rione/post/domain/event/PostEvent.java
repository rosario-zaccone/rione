package com.rione.post.domain.event;

import java.time.LocalDateTime;

import com.rione.post.domain.model.PostId;

public sealed interface PostEvent permits PostCreated, PostContentUpdated, PostDeleted, CommentAdded, CommentUpdated,
		CommentRemoved, ReactionAdded, ReactionUpdated, ReactionRemoved {

	PostId postId();

	PostEventType type();

	LocalDateTime occurredAt();
}
