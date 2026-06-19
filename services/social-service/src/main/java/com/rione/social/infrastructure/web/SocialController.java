package com.rione.social.infrastructure.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rione.social.application.port.in.SocialService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping
class SocialController {

	private final SocialService socialService;

	SocialController(SocialService socialService) {
		this.socialService = socialService;
	}

	@PostMapping("/neighbor-requests")
	ResponseEntity<SocialService.NeighborRequestResponse> sendNeighborRequest(
			@Valid @RequestBody SendNeighborRequestRequest request) {
		SocialService.NeighborRequestResponse response = socialService
			.sendNeighborRequest(new SocialService.SendNeighborRequestCommand(request.senderId(), request.receiverId()));
		return ResponseEntity.created(URI.create("/neighbor-requests/" + response.id())).body(response);
	}

	@PostMapping("/neighbor-requests/{requestId}/acceptance")
	SocialService.NeighborRequestResponse acceptNeighborRequest(@PathVariable @Positive Long requestId) {
		return socialService.acceptNeighborRequest(requestId);
	}

	@PostMapping("/neighbor-requests/{requestId}/rejection")
	SocialService.NeighborRequestResponse rejectNeighborRequest(@PathVariable @Positive Long requestId) {
		return socialService.rejectNeighborRequest(requestId);
	}

	@GetMapping("/neighbor-requests/{requestId}")
	SocialService.NeighborRequestResponse getNeighborRequest(@PathVariable @Positive Long requestId) {
		return socialService.getNeighborRequest(requestId);
	}

	@GetMapping("/neighborships")
	List<SocialService.NeighborshipResponse> getNeighborships(@RequestParam @Positive Long followerId) {
		return socialService.getNeighborships(followerId);
	}

	@PostMapping("/blocks")
	ResponseEntity<SocialService.BlockResponse> blockUser(@Valid @RequestBody BlockUserRequest request) {
		SocialService.BlockResponse response = socialService
			.blockUser(new SocialService.BlockUserCommand(request.blockerId(), request.blockedId()));
		return ResponseEntity.created(URI.create("/blocks/" + response.id())).body(response);
	}

	@PostMapping("/blocks/removal")
	ResponseEntity<Void> unblockUser(@Valid @RequestBody UnblockUserRequest request) {
		socialService.unblockUser(new SocialService.UnblockUserCommand(request.blockerId(), request.blockedId()));
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/neighborhood-changes")
	ResponseEntity<Void> reconcileRelationshipsAfterNeighborhoodChange(
			@Valid @RequestBody NeighborhoodChangedRequest request) {
		socialService.reconcileRelationshipsAfterNeighborhoodChange(new SocialService.NeighborhoodChangedCommand(request.userId()));
		return ResponseEntity.accepted().build();
	}

	record SendNeighborRequestRequest(@NotNull @Positive Long senderId, @NotNull @Positive Long receiverId) {
	}

	record BlockUserRequest(@NotNull @Positive Long blockerId, @NotNull @Positive Long blockedId) {
	}

	record UnblockUserRequest(@NotNull @Positive Long blockerId, @NotNull @Positive Long blockedId) {
	}

	record NeighborhoodChangedRequest(@NotNull @Positive Long userId) {
	}
}
