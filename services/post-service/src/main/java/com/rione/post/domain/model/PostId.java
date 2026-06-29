package com.rione.post.domain.model;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record PostId(Long value) implements DomainId {

	public PostId {
		DomainId.validate(value, "Post id");
	}
}
