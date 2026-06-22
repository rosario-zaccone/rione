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
	ResponseEntity<SocialService.NeighborRequestResponse> sendNeighborRequest(
			@Valid @RequestBody SendNeighborRequestRequest request) {
		SocialService.NeighborRequestResponse response = socialService
			.sendNeighborRequest(new SocialService.SendNeighborRequestCommand(currentUser.id(), request.receiverId()));
		return ResponseEntity.created(URI.create("/neighbor-requests/" + response.id())).body(response);
	}

	@PostMapping("/neighbor-requests/{requestId}/acceptance")
	SocialService.NeighborRequestResponse acceptNeighborRequest(@PathVariable @Positive Long requestId) {
		return socialService.acceptNeighborRequest(requestId, currentUser.actor());
	}

	@PostMapping("/neighbor-requests/{requestId}/rejection")
	SocialService.NeighborRequestResponse rejectNeighborRequest(@PathVariable @Positive Long requestId) {
		return socialService.rejectNeighborRequest(requestId, currentUser.actor());
	}

	@GetMapping("/me/neighbor-requests/sent")
	List<SocialService.NeighborRequestResponse> getMySentNeighborRequests() {
		return socialService.getSentNeighborRequests(currentUser.id());
	}

	@GetMapping("/me/neighbor-requests/received")
	List<SocialService.NeighborRequestResponse> getMyReceivedNeighborRequests() {
		return socialService.getReceivedNeighborRequests(currentUser.id());
	}

	@GetMapping("/me/neighborships")
	List<SocialService.NeighborResponse> getMyNeighbors() {
		return socialService.getNeighbors(currentUser.id());
	}

	@GetMapping("/users")
	List<SocialService.UserSearchResponse> searchUsers(@RequestParam(defaultValue = "") String query) {
		return socialService.searchUsers(new SocialService.SearchUsersQuery(currentUser.id(), query));
	}

	@DeleteMapping("/me/neighborships/{neighborId}")
	ResponseEntity<Void> removeMyNeighborship(@PathVariable @Positive Long neighborId) {
		socialService.removeNeighborship(new SocialService.RemoveNeighborshipCommand(currentUser.id(), neighborId));
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me/blocks")
	List<SocialService.BlockResponse> getMyBlocks() {
		return socialService.getBlocks(currentUser.id());
	}

	@PutMapping("/me/blocks/{blockedId}")
	ResponseEntity<SocialService.BlockResponse> putMyBlock(@PathVariable @Positive Long blockedId) {
		SocialService.BlockOperationResult result = socialService
			.putBlock(new SocialService.BlockUserCommand(currentUser.id(), blockedId));
		return ResponseEntity.status(result.created() ? 201 : 200).body(result.block());
	}

	@DeleteMapping("/me/blocks/{blockedId}")
	ResponseEntity<Void> deleteMyBlock(@PathVariable @Positive Long blockedId) {
		socialService.unblockUser(new SocialService.UnblockUserCommand(currentUser.id(), blockedId));
		return ResponseEntity.noContent().build();
	}

	record SendNeighborRequestRequest(@NotNull @Positive Long receiverId) {
	}

}
