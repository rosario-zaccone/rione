package com.rione.user.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record Biography(String info) {

	public static final int MIN_LENGTH = 20;
	public static final int MAX_LENGTH = 500;

	public Biography {
		info = info == null ? "" : info.trim();
		if (info.length() < MIN_LENGTH) {
			throw new DomainException("Biography must contain at least " + MIN_LENGTH + " characters");
		}
		if (info.length() > MAX_LENGTH) {
			throw new DomainException("Biography cannot exceed " + MAX_LENGTH + " characters");
		}
	}
}
