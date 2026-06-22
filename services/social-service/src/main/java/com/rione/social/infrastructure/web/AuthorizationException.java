package com.rione.social.infrastructure.web;

public class AuthorizationException extends RuntimeException {

	private final boolean unauthenticated;

	public AuthorizationException(String message, boolean unauthenticated) {
		super(message);
		this.unauthenticated = unauthenticated;
	}

	boolean isUnauthenticated() {
		return unauthenticated;
	}
}
