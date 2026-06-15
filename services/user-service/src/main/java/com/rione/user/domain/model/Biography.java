package com.rione.user.domain.model;

@DDDValueObject
public record Biography(String info) {

	public static final int MAX_LENGTH = 500;

	public Biography {
		info = info == null ? "" : info.trim();
		if (info.length() > MAX_LENGTH) {
			throw new DomainException("Biography cannot exceed " + MAX_LENGTH + " characters");
		}
	}
}
