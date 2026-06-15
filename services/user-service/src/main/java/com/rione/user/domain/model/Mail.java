package com.rione.user.domain.model;

import java.util.Locale;
import java.util.regex.Pattern;

@DDDValueObject
public record Mail(String mail) {

	private static final Pattern MAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	public Mail {
		if (mail == null || mail.isBlank()) {
			throw new DomainException("Mail is required");
		}
		mail = mail.trim().toLowerCase(Locale.ROOT);
		if (!MAIL_PATTERN.matcher(mail).matches()) {
			throw new DomainException("Mail must be valid");
		}
	}
}
