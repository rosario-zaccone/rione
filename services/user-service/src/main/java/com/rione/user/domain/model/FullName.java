package com.rione.user.domain.model;

@DDDValueObject
public record FullName(String name, String surname) {

	public FullName {
		name = normalizeRequired(name, "Name");
		surname = normalizeRequired(surname, "Surname");
	}

	public String displayName() {
		return name + " " + surname;
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
