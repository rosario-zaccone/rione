package com.rione.user.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record Password(String hash) {

	public Password {
		if (hash == null || hash.isBlank()) {
			throw new DomainException("Password hash is required");
		}
	}
}
