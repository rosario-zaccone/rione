package com.rione.social.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import com.rione.common.application.InPort;

@InPort
public interface SocialService {

	NeighborRequestResponse sendNeighborRequest(SendNeighborRequestCommand command);

	NeighborRequestResponse acceptNeighborRequest(Long requestId);

	NeighborRequestResponse rejectNeighborRequest(Long requestId);

	NeighborRequestResponse getNeighborRequest(Long requestId);

	List<NeighborshipResponse> getNeighborships(Long followerId);

	BlockResponse blockUser(BlockUserCommand command);

	void unblockUser(UnblockUserCommand command);

	void reconcileRelationshipsAfterNeighborhoodChange(NeighborhoodChangedCommand command);

	record SendNeighborRequestCommand(Long senderId, Long receiverId) {
	}

	record BlockUserCommand(Long blockerId, Long blockedId) {
	}

	record UnblockUserCommand(Long blockerId, Long blockedId) {
	}

	record NeighborhoodChangedCommand(Long userId) {
	}

	record NeighborRequestResponse(Long id, Long senderId, Long receiverId, LocalDateTime date, String status) {
	}

	record NeighborshipResponse(Long id, Long followerId, Long followedId, LocalDateTime date) {
	}

	record BlockResponse(Long id, Long blockerId, Long blockedId) {
	}
}
