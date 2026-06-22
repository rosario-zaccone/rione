package com.rione.social.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rione.social.application.service.SocialApplicationException;
import com.rione.social.application.service.SocialAuthorizationException;
import com.rione.social.application.service.SocialNotFoundException;
import com.rione.social.domain.model.DomainException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
class SocialExceptionHandler {

	@ExceptionHandler(SocialApplicationException.class)
	ProblemDetail handleApplication(SocialApplicationException exception) {
		HttpStatus status = exception.getMessage().contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
		return ProblemDetail.forStatusAndDetail(status, exception.getMessage());
	}

	@ExceptionHandler(SocialNotFoundException.class)
	ProblemDetail handleNotFound(SocialNotFoundException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(SocialAuthorizationException.class)
	ProblemDetail handleAuthorization(SocialAuthorizationException exception) {
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

	@ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
	ProblemDetail handleValidation(Exception exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
	}
}
