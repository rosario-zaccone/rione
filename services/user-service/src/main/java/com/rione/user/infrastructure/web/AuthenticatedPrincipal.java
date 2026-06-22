package com.rione.user.infrastructure.web;

import java.security.Principal;

public record AuthenticatedPrincipal(String subject, boolean admin, boolean service) implements Principal {

	public Long userId() {
		if (service) {
			throw new AuthorizationException("A service identity is not a user", false);
		}
		return Long.valueOf(subject);
	}

	@Override
	public String getName() {
		return subject;
	}
}
