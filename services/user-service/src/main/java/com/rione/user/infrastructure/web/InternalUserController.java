package com.rione.user.infrastructure.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rione.user.application.port.in.UserService;

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
	UserService.UserNeighborhoodResponse getUserNeighborhood(@PathVariable @Positive Long userId) {
		return userService.getUserNeighborhood(userId);
	}

	@GetMapping("/search")
	List<UserService.UserDirectoryResponse> searchUsers(@RequestParam @Positive Long neighborhoodId,
			@RequestParam(defaultValue = "") String query) {
		return userService.searchUsers(neighborhoodId, query);
	}
}
