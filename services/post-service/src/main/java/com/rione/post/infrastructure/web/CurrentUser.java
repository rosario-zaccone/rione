package com.rione.post.infrastructure.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.rione.post.application.port.in.PostService;

@Component
public class CurrentUser {

	public Long id() {
		return principal().userId();
	}

	public PostService.Actor actor() {
		return new PostService.Actor(id());
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
