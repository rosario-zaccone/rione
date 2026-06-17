package com.rione.social.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record UserId(Long value) {

	public UserId {
		if (value == null || value <= 0) {
			throw new DomainException("User id must be positive");
		}
	}
}
