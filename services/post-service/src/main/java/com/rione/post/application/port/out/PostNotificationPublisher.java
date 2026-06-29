package com.rione.post.application.port.out;

import com.rione.common.application.OutPort;
import com.rione.post.domain.event.CommentAdded;
import com.rione.post.domain.event.ReactionAdded;
import com.rione.post.domain.model.UserId;

@OutPort
public interface PostNotificationPublisher {

	void publishCommentAdded(CommentAdded event, UserId postAuthor);

	void publishReactionAdded(ReactionAdded event, UserId postAuthor);
}
