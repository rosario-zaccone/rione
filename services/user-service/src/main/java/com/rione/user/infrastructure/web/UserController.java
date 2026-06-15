package com.rione.user.infrastructure.web;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rione.user.application.port.in.UserService;
import com.rione.user.application.port.in.UserService.LogInCommand;
import com.rione.user.application.port.in.UserService.SignUpCommand;
import com.rione.user.application.port.in.UserService.UpdateProfileCommand;
import com.rione.user.application.port.in.UserService.UserResponse;
import com.rione.user.application.service.UserApplicationException;
import com.rione.user.domain.model.DomainException;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	ResponseEntity<UserResponse> signUp(@RequestBody SignUpRequest request) {
		UserResponse user = userService.signUp(new SignUpCommand(request.name(), request.surname(), request.username(),
				request.mail(), request.password(), request.neighborhoodId(), request.neighborhoodName(), request.city(),
				request.country(), request.birthDate(), request.bio()));
		return ResponseEntity.status(HttpStatus.CREATED).body(user);
	}

	@PostMapping("/login")
	UserResponse logIn(@RequestBody LogInRequest request) {
		return userService.logIn(new LogInCommand(request.mail(), request.password()));
	}

	@GetMapping("/{userId}")
	UserResponse getUser(@PathVariable Long userId) {
		return userService.getUser(userId);
	}

	@PutMapping("/{userId}/profile")
	UserResponse updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileRequest request) {
		return userService.updateProfile(new UpdateProfileCommand(userId, request.name(), request.surname(),
				request.username(), request.neighborhoodId(), request.neighborhoodName(), request.city(),
				request.country(), request.birthDate(), request.bio()));
	}

	@ExceptionHandler({ DomainException.class, UserApplicationException.class })
	ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
	}

	record SignUpRequest(String name, String surname, String username, String mail, String password, Long neighborhoodId,
			String neighborhoodName, String city, String country, LocalDateTime birthDate, String bio) {
	}

	record LogInRequest(String mail, String password) {
	}

	record UpdateProfileRequest(String name, String surname, String username, Long neighborhoodId,
			String neighborhoodName, String city, String country, LocalDateTime birthDate, String bio) {
	}

	record ErrorResponse(String message) {
	}
}
