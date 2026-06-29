package com.rione.post.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record PostContent(String content) {

	public static final int MIN_NON_WHITESPACE_LENGTH = 20;

	public PostContent {
		content = normalize(content, "Post content", MIN_NON_WHITESPACE_LENGTH);
	}

	private static String normalize(String value, String fieldName, int minimumLength) {
		if (value == null || value.isBlank()) {
			throw new DomainException(fieldName + " is required");
		}
		String normalized = value.trim();
		if (nonWhitespaceLength(normalized) < minimumLength) {
			throw new DomainException(fieldName + " must contain at least " + minimumLength
					+ " non-whitespace characters");
		}
		return normalized;
	}

	private static long nonWhitespaceLength(String value) {
		return value.chars().filter(character -> !Character.isWhitespace(character)).count();
	}
}
