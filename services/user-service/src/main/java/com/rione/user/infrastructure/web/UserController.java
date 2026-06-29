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

import io.swagger.v3.oas.annotations.Operation;
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
	@Operation(summary = "Register a user",
			description = "Anyone can access this endpoint. It creates a new user account with unique mail and username and stores private account data for authenticated account operations.")
	ResponseEntity<UserService.UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
		UserService.UserResponse response = userService.signUp(new UserService.SignUpCommand(request.name(),
				request.surname(), request.username(), request.mail(), request.password(), request.neighborhoodId(),
				request.birthDate(), request.bio()));
		return ResponseEntity.created(URI.create("/users/" + response.id())).body(response);
	}

	@PostMapping("/login")
	@Operation(summary = "Log in",
			description = "Anyone can access this endpoint. It authenticates user credentials and returns a JWT for subsequent authenticated requests; failed credentials do not reveal whether the mail exists.")
	LogInResponse logIn(@Valid @RequestBody LogInRequest request) {
		UserService.UserResponse user = userService.logIn(new UserService.LogInCommand(request.mail(), request.password()));
		return new LogInResponse(jwtService.createToken(user.id(), user.admin()), user);
	}

	@PostMapping("/logout")
	@Operation(summary = "Log out",
			description = "Only authenticated users can access this endpoint. It revokes the current bearer token so it can no longer be used.")
	ResponseEntity<Void> logOut() {
		jwtService.revoke(currentUser.token());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	@Operation(summary = "Get current user",
			description = "Only authenticated users can access this endpoint. It returns the authenticated user's own profile, including private account fields needed by the owner.")
	UserService.UserResponse getCurrentUser() {
		return userService.getUser(currentUser.id());
	}

	@GetMapping("/{userId}/public-profile")
	@Operation(summary = "Get public user profile",
			description = "Only authenticated users can access this endpoint. It returns public profile information for the selected user and excludes private account data such as mail, birth date, password state, and admin flags.")
	UserService.PublicUserProfileResponse getPublicUserProfile(@PathVariable @Positive Long userId) {
		return userService.getPublicUserProfile(userId);
	}

	@PutMapping("/me/profile")
	@Operation(summary = "Update current user profile",
			description = "Only authenticated users can access this endpoint. Users can update only their own profile; username uniqueness is enforced and private account ownership is resolved from the bearer token.")
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
