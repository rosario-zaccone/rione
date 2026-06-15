package com.rione.user.domain.model;

@DDDValueObject
public record UserId(Long value) {

	public UserId {
		if (value == null || value <= 0) {
			throw new DomainException("User id must be positive");
		}
	}
}
