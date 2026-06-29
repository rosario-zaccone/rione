package com.rione.post.application.port.out;

import java.util.List;
import java.time.LocalDate;

import com.rione.common.application.OutPort;
import com.rione.post.domain.event.PostEvent;
import com.rione.post.domain.event.PostEventType;
import com.rione.post.domain.model.CommentId;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.ReactionId;

@OutPort
public interface PostEventStore {

	PostId nextPostId();

	CommentId nextCommentId();

	ReactionId nextReactionId();

	List<PostEvent> read(PostId postId);

	List<PostEvent> readAll();

	long countEvents(PostEventType type);

	long countEventsSince(PostEventType type, LocalDate date);

	void append(PostId postId, long expectedVersion, List<PostEvent> events);
}
