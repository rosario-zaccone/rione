package com.rione.social.domain.model;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record UserId(Long value) implements DomainId {

	public UserId {
		DomainId.validate(value, "User id");
	}
}
