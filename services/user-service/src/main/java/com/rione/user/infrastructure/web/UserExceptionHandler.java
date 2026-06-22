package com.rione.user.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rione.user.application.service.UserApplicationException;
import com.rione.user.domain.model.DomainException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class UserExceptionHandler {

	@ExceptionHandler(UserApplicationException.class)
	ProblemDetail handleApplication(UserApplicationException exception) {
		HttpStatus status = statusFor(exception);
		return ProblemDetail.forStatusAndDetail(status, exception.getMessage());
	}

	@ExceptionHandler(DomainException.class)
	ProblemDetail handleDomain(DomainException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(AuthorizationException.class)
	ProblemDetail handleAuthorization(AuthorizationException exception) {
		HttpStatus status = exception.isUnauthenticated() ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
		return ProblemDetail.forStatusAndDetail(status, exception.getMessage());
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
	ProblemDetail handleValidation(Exception exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
	}

	private HttpStatus statusFor(UserApplicationException exception) {
		String message = exception.getMessage();
		if (message.contains("not found")) {
			return HttpStatus.NOT_FOUND;
		}
		if (message.startsWith("Only admin users")) {
			return HttpStatus.FORBIDDEN;
		}
		if (message.contains("cannot be removed while users belong")) {
			return HttpStatus.CONFLICT;
		}
		return HttpStatus.BAD_REQUEST;
	}
}
