package com.rione.social.infrastructure.web;

import java.security.Principal;

public record AuthenticatedPrincipal(Long userId, boolean admin, boolean service) implements Principal {

	public AuthenticatedPrincipal(Long userId, boolean admin) {
		this(userId, admin, false);
	}

	@Override
	public String getName() {
		if (service) {
			return "service";
		}
		return userId.toString();
	}
}
