package com.rione.post.domain.model;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record ReactionId(Long value) implements DomainId {

	public ReactionId {
		DomainId.validate(value, "Reaction id");
	}
}
