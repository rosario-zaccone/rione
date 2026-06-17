package com.rione.social.infrastructure.web;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rione.social.application.port.in.SocialService;
import com.rione.social.application.port.in.SocialService.BlockUserCommand;
import com.rione.social.application.port.in.SocialService.BlockResponse;
import com.rione.social.application.port.in.SocialService.NeighborRequestResponse;
import com.rione.social.application.port.in.SocialService.NeighborshipResponse;
import com.rione.social.application.port.in.SocialService.SendNeighborRequestCommand;
import com.rione.social.application.service.SocialApplicationException;
import com.rione.social.domain.model.DomainException;

@RestController
@RequestMapping
class SocialController {

	private final SocialService socialService;

	SocialController(SocialService socialService) {
		this.socialService = socialService;
	}

	@PostMapping("/neighbor-requests")
	ResponseEntity<NeighborRequestResponse> sendNeighborRequest(@RequestBody SendNeighborRequestRequest request) {
		NeighborRequestResponse response = socialService
			.sendNeighborRequest(new SendNeighborRequestCommand(request.senderId(), request.receiverId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/neighbor-requests/{requestId}/acceptance")
	NeighborRequestResponse acceptNeighborRequest(@PathVariable Long requestId) {
		return socialService.acceptNeighborRequest(requestId);
	}

	@PostMapping("/neighbor-requests/{requestId}/rejection")
	NeighborRequestResponse rejectNeighborRequest(@PathVariable Long requestId) {
		return socialService.rejectNeighborRequest(requestId);
	}

	@GetMapping("/neighbor-requests/{requestId}")
	NeighborRequestResponse getNeighborRequest(@PathVariable Long requestId) {
		return socialService.getNeighborRequest(requestId);
	}

	@GetMapping("/neighborships")
	List<NeighborshipResponse> getNeighborships(@RequestParam Long followerId) {
		return socialService.getNeighborships(followerId);
	}

	@PostMapping("/blocks")
	ResponseEntity<BlockResponse> blockUser(@RequestBody BlockUserRequest request) {
		BlockResponse response = socialService.blockUser(new BlockUserCommand(request.blockerId(), request.blockedId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@ExceptionHandler({ DomainException.class, SocialApplicationException.class })
	ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException exception) {
		return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
	}

	record SendNeighborRequestRequest(Long senderId, Long receiverId) {
	}

	record BlockUserRequest(Long blockerId, Long blockedId) {
	}

	record ErrorResponse(String message, LocalDateTime timestamp) {
		ErrorResponse(String message) {
			this(message, LocalDateTime.now());
		}
	}
}
