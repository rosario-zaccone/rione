package com.rione.post.application.service;

public class PostNotFoundException extends RuntimeException {

	public PostNotFoundException(String message) {
		super(message);
	}
}
