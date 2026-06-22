package com.rione.user.infrastructure.web;

public class AuthorizationException extends RuntimeException {

	private final boolean unauthenticated;

	AuthorizationException(String message, boolean unauthenticated) {
		super(message);
		this.unauthenticated = unauthenticated;
	}

	boolean isUnauthenticated() {
		return unauthenticated;
	}
}
