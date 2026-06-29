package com.rione.user.infrastructure.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rione.user.application.port.in.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/internal/users")
@PreAuthorize("hasRole('SERVICE')")
class InternalUserController {

	private final UserService userService;

	InternalUserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/{userId}/neighborhood")
	@Operation(summary = "Get user neighborhood for internal services",
			description = "Only authenticated service tokens can access this endpoint. It returns the minimum neighborhood data needed by other services for authorization and privacy decisions.")
	UserService.UserNeighborhoodResponse getUserNeighborhood(@PathVariable @Positive Long userId) {
		return userService.getUserNeighborhood(userId);
	}

	@GetMapping("/profiles")
	@Operation(summary = "Get user directory profiles for internal services",
			description = "Only authenticated service tokens can access this endpoint. It returns minimal public identity fields for the requested users and excludes private account data.")
	List<UserService.UserDirectoryResponse> getUserProfiles(@RequestParam List<@Positive Long> ids) {
		return userService.getUserProfiles(ids);
	}

	@GetMapping("/search")
	@Operation(summary = "Search user directory for internal services",
			description = "Only authenticated service tokens can access this endpoint. It returns minimal public identity fields scoped to a neighborhood for social discovery.")
	List<UserService.UserDirectoryResponse> searchUsers(@RequestParam @Positive Long neighborhoodId,
			@RequestParam(defaultValue = "") String query) {
		return userService.searchUsers(neighborhoodId, query);
	}
}
