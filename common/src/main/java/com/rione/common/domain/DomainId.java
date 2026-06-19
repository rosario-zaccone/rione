package com.rione.common.domain;

public interface DomainId {

	Long value();

	default void validate(String name) {
		validate(value(), name);
	}

	static void validate(Long value, String name) {
		if (value == null || value <= 0) {
			throw new IllegalArgumentException(name + " must be positive");
		}
	}
}
