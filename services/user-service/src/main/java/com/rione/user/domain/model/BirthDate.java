package com.rione.user.domain.model;

import com.rione.common.domain.DDDValueObject;

import java.time.LocalDateTime;

@DDDValueObject
public record BirthDate(LocalDateTime value) {

	public BirthDate {
		if (value == null || value.isAfter(LocalDateTime.now())) {
			throw new DomainException("Birth date must be in the past");
		}
	}
}
