package com.rione.post.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rione.post.application.service.PostApplicationException;
import com.rione.post.application.service.PostAuthorizationException;
import com.rione.post.application.service.PostNotFoundException;
import com.rione.post.domain.model.DomainException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
class PostExceptionHandler {

	@ExceptionHandler(PostApplicationException.class)
	ProblemDetail handleApplication(PostApplicationException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler(PostNotFoundException.class)
	ProblemDetail handleNotFound(PostNotFoundException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(PostAuthorizationException.class)
	ProblemDetail handleAuthorization(PostAuthorizationException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
	}

	@ExceptionHandler(AuthorizationException.class)
	ProblemDetail handleWebAuthorization(AuthorizationException exception) {
		HttpStatus status = exception.isUnauthenticated() ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
		return ProblemDetail.forStatusAndDetail(status, exception.getMessage());
	}

	@ExceptionHandler(DomainException.class)
	ProblemDetail handleDomain(DomainException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
	ProblemDetail handleValidation(Exception exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
	}
}
