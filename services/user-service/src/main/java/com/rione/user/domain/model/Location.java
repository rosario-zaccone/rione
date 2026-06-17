package com.rione.user.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record Location(String city, String country) {

	public Location {
		city = normalizeRequired(city, "City");
		country = normalizeRequired(country, "Country");
	}

	private static String normalizeRequired(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new DomainException(fieldName + " is required");
		}
		String normalized = value.trim();
		if (normalized.length() > 80) {
			throw new DomainException(fieldName + " cannot exceed 80 characters");
		}
		return normalized;
	}
}
