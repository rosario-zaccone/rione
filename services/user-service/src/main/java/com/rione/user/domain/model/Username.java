package com.rione.user.domain.model;

import com.rione.common.domain.DDDValueObject;

import java.util.Locale;
import java.util.regex.Pattern;

@DDDValueObject
public record Username(String value) {

	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9][a-z0-9._-]{2,39}$");

	public Username {
		if (value == null || value.isBlank()) {
			throw new DomainException("Username is required");
		}
		value = value.trim().toLowerCase(Locale.ROOT);
		if (!USERNAME_PATTERN.matcher(value).matches()) {
			throw new DomainException("Username must be 3-40 characters and use letters, digits, '.', '_' or '-'");
		}
	}
}
