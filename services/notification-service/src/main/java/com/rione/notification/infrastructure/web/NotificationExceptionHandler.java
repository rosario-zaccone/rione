package com.rione.notification.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rione.notification.application.service.NotificationNotFoundException;
import com.rione.notification.domain.DomainException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
class NotificationExceptionHandler {

	@ExceptionHandler(NotificationNotFoundException.class)
	ProblemDetail handleNotFound(NotificationNotFoundException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler({ DomainException.class, IllegalArgumentException.class })
	ProblemDetail handleBadRequest(RuntimeException exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
	ProblemDetail handleValidation(Exception exception) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
	}
}
