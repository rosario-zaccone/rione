package com.rione.user.infrastructure.web;

import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rione.user.application.port.in.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	ResponseEntity<UserService.UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
		UserService.UserResponse response = userService.signUp(new UserService.SignUpCommand(request.name(),
				request.surname(), request.username(), request.mail(), request.password(), request.neighborhoodId(),
				request.birthDate(), request.bio()));
		return ResponseEntity.created(URI.create("/users/" + response.id())).body(response);
	}

	@PostMapping("/login")
	UserService.UserResponse logIn(@Valid @RequestBody LogInRequest request) {
		return userService.logIn(new UserService.LogInCommand(request.mail(), request.password()));
	}

	@GetMapping("/{userId}")
	UserService.UserResponse getUser(@PathVariable @Positive Long userId) {
		return userService.getUser(userId);
	}

	@PutMapping("/{userId}/profile")
	UserService.UserResponse updateProfile(@PathVariable @Positive Long userId,
			@Valid @RequestBody UpdateProfileRequest request) {
		return userService.updateProfile(new UserService.UpdateProfileCommand(userId, request.name(), request.surname(),
				request.username(), request.neighborhoodId(), request.birthDate(), request.bio()));
	}

	record SignUpRequest(@NotBlank @Size(max = 80) String name, @NotBlank @Size(max = 80) String surname,
			@NotBlank @Size(min = 3, max = 40) String username, @NotBlank @Email String mail,
			@NotBlank @Size(max = 255) String password, @NotNull @Positive Long neighborhoodId,
			@NotNull LocalDateTime birthDate, @NotBlank @Size(min = 20, max = 500) String bio) {
	}

	record LogInRequest(@NotBlank @Email String mail, @NotBlank String password) {
	}

	record UpdateProfileRequest(@NotBlank @Size(max = 80) String name, @NotBlank @Size(max = 80) String surname,
			@NotBlank @Size(min = 3, max = 40) String username, @NotNull @Positive Long neighborhoodId,
			@NotNull LocalDateTime birthDate, @NotBlank @Size(min = 20, max = 500) String bio) {
	}
}
