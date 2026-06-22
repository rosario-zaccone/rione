package com.rione.user.infrastructure.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

	public Long id() {
		return principal().userId();
	}

	public String token() {
		Authentication authentication = authentication();
		if (!(authentication.getCredentials() instanceof String token) || token.isBlank()) {
			throw new AuthorizationException("Authentication is invalid", true);
		}
		return token;
	}

	private AuthenticatedPrincipal principal() {
		Authentication authentication = authentication();
		Object principal = authentication.getPrincipal();
		if (principal instanceof AuthenticatedPrincipal authenticatedPrincipal) {
			return authenticatedPrincipal;
		}
		throw new AuthorizationException("Authentication is invalid", true);
	}

	private Authentication authentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AuthorizationException("Authentication is required", true);
		}
		return authentication;
	}
}
