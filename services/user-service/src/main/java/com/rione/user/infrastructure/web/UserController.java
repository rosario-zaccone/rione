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
	private final JwtService jwtService;
	private final CurrentUser currentUser;

	public UserController(UserService userService, JwtService jwtService, CurrentUser currentUser) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.currentUser = currentUser;
	}

	@PostMapping
	ResponseEntity<UserService.UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
		UserService.UserResponse response = userService.signUp(new UserService.SignUpCommand(request.name(),
				request.surname(), request.username(), request.mail(), request.password(), request.neighborhoodId(),
				request.birthDate(), request.bio()));
		return ResponseEntity.created(URI.create("/users/" + response.id())).body(response);
	}

	@PostMapping("/login")
	LogInResponse logIn(@Valid @RequestBody LogInRequest request) {
		UserService.UserResponse user = userService.logIn(new UserService.LogInCommand(request.mail(), request.password()));
		return new LogInResponse(jwtService.createToken(user.id(), user.admin()), user);
	}

	@GetMapping("/me")
	UserService.UserResponse getCurrentUser() {
		return userService.getUser(currentUser.id());
	}

	@PutMapping("/me/profile")
	UserService.UserResponse updateCurrentUserProfile(@Valid @RequestBody UpdateProfileRequest request) {
		Long userId = currentUser.id();
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

	record LogInResponse(String token, UserService.UserResponse user) {
	}

	record UpdateProfileRequest(@NotBlank @Size(max = 80) String name, @NotBlank @Size(max = 80) String surname,
			@NotBlank @Size(min = 3, max = 40) String username, @NotNull @Positive Long neighborhoodId,
			@NotNull LocalDateTime birthDate, @NotBlank @Size(min = 20, max = 500) String bio) {
	}
}
