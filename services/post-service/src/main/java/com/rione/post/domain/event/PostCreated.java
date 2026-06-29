package com.rione.post.domain.event;

import java.time.LocalDateTime;

import com.rione.post.domain.model.NeighborhoodId;
import com.rione.post.domain.model.Place;
import com.rione.post.domain.model.PostContent;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.PostType;
import com.rione.post.domain.model.PostVisibility;
import com.rione.post.domain.model.UserId;

public record PostCreated(PostId postId, UserId author, NeighborhoodId neighborhoodId, PostContent content,
		Place place, PostType postType, PostVisibility visibility, LocalDateTime occurredAt) implements PostEvent {

	@Override
	public PostEventType type() {
		return PostEventType.POST_CREATED;
	}
}
