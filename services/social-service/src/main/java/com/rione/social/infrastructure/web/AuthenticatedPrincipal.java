package com.rione.social.infrastructure.web;

import java.security.Principal;

public record AuthenticatedPrincipal(Long userId, boolean admin) implements Principal {

	@Override
	public String getName() {
		return userId.toString();
	}
}
