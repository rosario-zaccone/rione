package com.rione.social.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rione.social.application.port.in.SocialService;
import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.application.port.out.NeighborhoodMembership;
import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.NeighborRequestId;
import com.rione.social.domain.model.Neighborship;
import com.rione.social.domain.model.UserId;

@Service
public class SocialServiceImpl implements SocialService {

	private final NeighborRequestRepository neighborRequests;
	private final NeighborshipRepository neighborships;
	private final BlockRepository blocks;
	private final NeighborhoodMembership neighborhoodMembership;

	public SocialServiceImpl(NeighborRequestRepository neighborRequests, NeighborshipRepository neighborships,
			BlockRepository blocks, NeighborhoodMembership neighborhoodMembership) {
		this.neighborRequests = neighborRequests;
		this.neighborships = neighborships;
		this.blocks = blocks;
		this.neighborhoodMembership = neighborhoodMembership;
	}

	@Override
	public NeighborRequestResponse sendNeighborRequest(SendNeighborRequestCommand command) {
		UserId sender = new UserId(command.senderId());
		UserId receiver = new UserId(command.receiverId());
		NeighborRequest request = NeighborRequest.create(sender, receiver, LocalDateTime.now());
		if (!neighborhoodMembership.sameNeighborhood(sender, receiver)) {
			throw new SocialApplicationException("Users must belong to the same neighborhood");
		}
		if (blocks.existsBetween(sender, receiver) || blocks.existsBetween(receiver, sender)) {
			throw new SocialApplicationException("Neighbor request cannot be sent between blocked users");
		}
		if (neighborships.exists(sender, receiver)) {
			throw new SocialApplicationException("Users are already neighbours");
		}
		if (neighborRequests.existsPendingBetween(sender, receiver)) {
			throw new SocialApplicationException("A pending neighbor request already exists between these users");
		}
		return toResponse(neighborRequests.save(request));
	}

	@Override
	public NeighborRequestResponse acceptNeighborRequest(Long requestId) {
		NeighborRequest request = findRequest(requestId);
		if (!neighborhoodMembership.sameNeighborhood(request.sender(), request.receiver())) {
			throw new SocialApplicationException("Users must belong to the same neighborhood");
		}
		request.accept();
		neighborRequests.save(request);
		createNeighborshipIfMissing(request.sender(), request.receiver());
		createNeighborshipIfMissing(request.receiver(), request.sender());
		return toResponse(request);
	}

	@Override
	public NeighborRequestResponse rejectNeighborRequest(Long requestId) {
		NeighborRequest request = findRequest(requestId);
		request.reject();
		return toResponse(neighborRequests.save(request));
	}

	@Override
	public NeighborRequestResponse getNeighborRequest(Long requestId) {
		return toResponse(findRequest(requestId));
	}

	@Override
	public List<NeighborshipResponse> getNeighborships(Long followerId) {
		return neighborships.findByFollower(new UserId(followerId)).stream().map(this::toResponse).toList();
	}

	@Override
	public BlockResponse blockUser(BlockUserCommand command) {
		UserId blocker = new UserId(command.blockerId());
		UserId blocked = new UserId(command.blockedId());
		if (blocks.existsBetween(blocker, blocked)) {
			throw new SocialApplicationException("User is already blocked");
		}
		neighborships.deleteBetween(blocker, blocked);
		Block block = Block.create(blocker, blocked);
		return toResponse(blocks.save(block));
	}

	@Override
	public void unblockUser(UnblockUserCommand command) {
		UserId blocker = new UserId(command.blockerId());
		UserId blocked = new UserId(command.blockedId());
		blocks.deleteBetween(blocker, blocked);
	}

	@Override
	public void reconcileRelationshipsAfterNeighborhoodChange(NeighborhoodChangedCommand command) {
		UserId user = new UserId(command.userId());
		removeInvalidNeighborships(user);
		removeInvalidPendingRequests(user);
	}

	private void removeInvalidNeighborships(UserId user) {
		neighborships.findByParticipant(user)
			.stream()
			.map(neighborship -> counterpartOf(neighborship, user))
			.filter(counterpart -> !neighborhoodMembership.sameNeighborhood(user, counterpart))
			.distinct()
			.forEach(counterpart -> neighborships.deleteBetween(user, counterpart));
	}

	private void removeInvalidPendingRequests(UserId user) {
		neighborRequests.findPendingInvolving(user)
			.stream()
			.filter(request -> !neighborhoodMembership.sameNeighborhood(user, request.counterpartOf(user)))
			.forEach(request -> neighborRequests.delete(request.id()));
	}

	private UserId counterpartOf(Neighborship neighborship, UserId user) {
		return neighborship.follower().equals(user) ? neighborship.followed() : neighborship.follower();
	}

	private NeighborRequest findRequest(Long requestId) {
		return neighborRequests.findById(new NeighborRequestId(requestId))
			.orElseThrow(() -> new SocialApplicationException("Neighbor request not found"));
	}

	private void createNeighborshipIfMissing(UserId follower, UserId followed) {
		if (!neighborships.exists(follower, followed)) {
			neighborships.save(Neighborship.create(follower, followed, LocalDateTime.now()));
		}
	}

	private NeighborRequestResponse toResponse(NeighborRequest request) {
		return new NeighborRequestResponse(request.id().value(), request.sender().value(), request.receiver().value(),
				request.date(), request.status().name());
	}

	private NeighborshipResponse toResponse(Neighborship neighborship) {
		return new NeighborshipResponse(neighborship.id().value(), neighborship.follower().value(),
				neighborship.followed().value(), neighborship.date());
	}

	private BlockResponse toResponse(Block block) {
		return new BlockResponse(block.id().value(), block.blocker().value(), block.blocked().value());
	}
}
