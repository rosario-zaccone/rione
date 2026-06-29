package com.rione.post.domain.model;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record CommentId(Long value) implements DomainId {

	public CommentId {
		DomainId.validate(value, "Comment id");
	}
}
