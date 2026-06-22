package com.rione.social.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import com.rione.common.application.InPort;

@InPort
public interface SocialService {

	NeighborRequestResponse sendNeighborRequest(SendNeighborRequestCommand command);

	NeighborRequestResponse acceptNeighborRequest(Long requestId, Actor actor);

	NeighborRequestResponse rejectNeighborRequest(Long requestId, Actor actor);

	List<NeighborRequestResponse> getSentNeighborRequests(Long userId);

	List<NeighborRequestResponse> getReceivedNeighborRequests(Long userId);

	List<NeighborResponse> getNeighbors(Long userId);

	void removeNeighborship(RemoveNeighborshipCommand command);

	BlockResponse blockUser(BlockUserCommand command);

	BlockOperationResult putBlock(BlockUserCommand command);

	List<BlockResponse> getBlocks(Long userId);

	void unblockUser(UnblockUserCommand command);

	void reconcileRelationshipsAfterNeighborhoodChange(NeighborhoodChangedCommand command);

	record SendNeighborRequestCommand(Long senderId, Long receiverId) {
	}

	record BlockUserCommand(Long blockerId, Long blockedId) {
	}

	record RemoveNeighborshipCommand(Long userId, Long neighborId) {
	}

	record UnblockUserCommand(Long blockerId, Long blockedId) {
	}

	record NeighborhoodChangedCommand(Long userId) {
	}

	record Actor(Long userId) {
	}

	record NeighborRequestResponse(Long id, Long senderId, Long receiverId, LocalDateTime date, String status) {
	}

	record NeighborResponse(Long id, Long userId, Long neighborId, LocalDateTime date) {
	}

	record BlockResponse(Long id, Long blockerId, Long blockedId) {
	}

	record BlockOperationResult(BlockResponse block, boolean created) {
	}
}
