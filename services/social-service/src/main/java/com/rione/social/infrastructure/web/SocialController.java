package com.rione.social.infrastructure.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rione.social.application.port.in.SocialService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping
class SocialController {

	private final SocialService socialService;
	private final CurrentUser currentUser;

	SocialController(SocialService socialService, CurrentUser currentUser) {
		this.socialService = socialService;
		this.currentUser = currentUser;
	}

	@PostMapping("/neighbor-requests")
	@Operation(summary = "Send neighbor request",
			description = "Only authenticated users can access this endpoint. The authenticated user sends a request to another user, subject to block, duplicate, and neighborhood rules; creating the request publishes a notification for the recipient.")
	ResponseEntity<SocialService.NeighborRequestResponse> sendNeighborRequest(
			@Valid @RequestBody SendNeighborRequestRequest request) {
		SocialService.NeighborRequestResponse response = socialService
			.sendNeighborRequest(new SocialService.SendNeighborRequestCommand(currentUser.id(), request.receiverId()));
		return ResponseEntity.created(URI.create("/neighbor-requests/" + response.id())).body(response);
	}

	@PostMapping("/neighbor-requests/{requestId}/acceptance")
	@Operation(summary = "Accept neighbor request",
			description = "Only authenticated users can access this endpoint. Only the recipient of the pending request can accept it; acceptance creates a neighborship and publishes a notification for the original sender.")
	SocialService.NeighborRequestResponse acceptNeighborRequest(@PathVariable @Positive Long requestId) {
		return socialService.acceptNeighborRequest(requestId, currentUser.actor());
	}

	@PostMapping("/neighbor-requests/{requestId}/rejection")
	@Operation(summary = "Reject neighbor request",
			description = "Only authenticated users can access this endpoint. Only the recipient of the pending request can reject it; rejection changes request state without creating a neighborship.")
	SocialService.NeighborRequestResponse rejectNeighborRequest(@PathVariable @Positive Long requestId) {
		return socialService.rejectNeighborRequest(requestId, currentUser.actor());
	}

	@GetMapping("/me/neighbor-requests/sent")
	@Operation(summary = "List sent neighbor requests",
			description = "Only authenticated users can access this endpoint. It returns requests sent by the authenticated user and excludes unrelated users' requests.")
	List<SocialService.NeighborRequestResponse> getMySentNeighborRequests() {
		return socialService.getSentNeighborRequests(currentUser.id());
	}

	@GetMapping("/me/neighbor-requests/received")
	@Operation(summary = "List received neighbor requests",
			description = "Only authenticated users can access this endpoint. It returns requests received by the authenticated user and excludes unrelated users' requests.")
	List<SocialService.NeighborRequestResponse> getMyReceivedNeighborRequests() {
		return socialService.getReceivedNeighborRequests(currentUser.id());
	}

	@GetMapping("/me/neighborships")
	@Operation(summary = "List my neighbours",
			description = "Only authenticated users can access this endpoint. It returns active neighborships involving the authenticated user and excludes blocked or unrelated users.")
	List<SocialService.NeighborResponse> getMyNeighbors() {
		return socialService.getNeighbors(currentUser.id());
	}

	@GetMapping("/users")
	@Operation(summary = "Search users",
			description = "Only authenticated users can access this endpoint. Search is scoped by the requester's neighborhood and social privacy rules, and blocked users are excluded.")
	List<SocialService.UserSearchResponse> searchUsers(@RequestParam(defaultValue = "") String query) {
		return socialService.searchUsers(new SocialService.SearchUsersQuery(currentUser.id(), query));
	}

	@DeleteMapping("/me/neighborships/{neighborId}")
	@Operation(summary = "Remove my neighborship",
			description = "Only authenticated users can access this endpoint. The authenticated user can remove only their own neighborship with the selected neighbor.")
	ResponseEntity<Void> removeMyNeighborship(@PathVariable @Positive Long neighborId) {
		socialService.removeNeighborship(new SocialService.RemoveNeighborshipCommand(currentUser.id(), neighborId));
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me/blocks")
	@Operation(summary = "List my blocks",
			description = "Only authenticated users can access this endpoint. It returns block relationships owned by the authenticated user.")
	List<SocialService.BlockResponse> getMyBlocks() {
		return socialService.getBlocks(currentUser.id());
	}

	@PutMapping("/me/blocks/{blockedId}")
	@Operation(summary = "Block a user",
			description = "Only authenticated users can access this endpoint. The authenticated user can block another user; blocking removes active neighborships and prevents direct social interactions.")
	ResponseEntity<SocialService.BlockResponse> putMyBlock(@PathVariable @Positive Long blockedId) {
		SocialService.BlockOperationResult result = socialService
			.putBlock(new SocialService.BlockUserCommand(currentUser.id(), blockedId));
		return ResponseEntity.status(result.created() ? 201 : 200).body(result.block());
	}

	@DeleteMapping("/me/blocks/{blockedId}")
	@Operation(summary = "Unblock a user",
			description = "Only authenticated users can access this endpoint. The authenticated user can remove only their own block; unblocking does not restore previous neighborships or requests.")
	ResponseEntity<Void> deleteMyBlock(@PathVariable @Positive Long blockedId) {
		socialService.unblockUser(new SocialService.UnblockUserCommand(currentUser.id(), blockedId));
		return ResponseEntity.noContent().build();
	}

	record SendNeighborRequestRequest(@NotNull @Positive Long receiverId) {
	}

}
