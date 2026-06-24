package com.rione.notification.infrastructure.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

	public Long id() {
		return principal().userId();
	}

	private AuthenticatedPrincipal principal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AuthorizationException("Authentication is required", true);
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof AuthenticatedPrincipal authenticatedPrincipal) {
			return authenticatedPrincipal;
		}
		throw new AuthorizationException("Authentication is invalid", true);
	}
}
