package com.rione.social.application.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import org.springframework.stereotype.Service;

import com.rione.social.application.port.in.SocialService;
import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.application.port.out.NeighborhoodMembership;
import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.application.port.out.SocialEventPublisher;
import com.rione.social.application.port.out.UserDirectory;
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
	private final UserDirectory userDirectory;
	private final SocialEventPublisher socialEvents;

	public SocialServiceImpl(NeighborRequestRepository neighborRequests, NeighborshipRepository neighborships,
			BlockRepository blocks, NeighborhoodMembership neighborhoodMembership, UserDirectory userDirectory,
			SocialEventPublisher socialEvents) {
		this.neighborRequests = neighborRequests;
		this.neighborships = neighborships;
		this.blocks = blocks;
		this.neighborhoodMembership = neighborhoodMembership;
		this.userDirectory = userDirectory;
		this.socialEvents = socialEvents;
	}

	@Override
	public List<UserSearchResponse> searchUsers(SearchUsersQuery query) {
		if (query.query() == null || query.query().isBlank()) {
			return List.of();
		}
		UserId requester = new UserId(query.requesterId());
		return userDirectory.searchInNeighborhood(requester, query.query().trim())
			.stream()
			.filter(profile -> !profile.id().equals(requester))
			.filter(profile -> !blocks.existsBetween(requester, profile.id()))
			.filter(profile -> !blocks.existsBetween(profile.id(), requester))
			.map(profile -> new UserSearchResponse(profile.id().value(), profile.name(), profile.surname(),
					profile.username()))
			.toList();
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
		NeighborRequest saved = neighborRequests.save(request);
		socialEvents.publishNeighborRequestReceived(saved);
		return toResponse(saved);
	}

	@Override
	public NeighborRequestResponse acceptNeighborRequest(Long requestId, Actor actor) {
		NeighborRequest request = findRequest(requestId);
		requireReceiver(request, actor);
		if (!neighborhoodMembership.sameNeighborhood(request.sender(), request.receiver())) {
			throw new SocialApplicationException("Users must belong to the same neighborhood");
		}
		request.accept();
		NeighborRequest saved = neighborRequests.save(request);
		createNeighborshipIfMissing(request.sender(), request.receiver());
		createNeighborshipIfMissing(request.receiver(), request.sender());
		socialEvents.publishNeighborRequestAccepted(saved);
		return toResponse(saved);
	}

	@Override
	public NeighborRequestResponse rejectNeighborRequest(Long requestId, Actor actor) {
		NeighborRequest request = findRequest(requestId);
		requireReceiver(request, actor);
		request.reject();
		return toResponse(neighborRequests.save(request));
	}

	@Override
	public List<NeighborRequestResponse> getSentNeighborRequests(Long userId) {
		return neighborRequests.findBySender(new UserId(userId)).stream().map(this::toResponse).toList();
	}

	@Override
	public List<NeighborRequestResponse> getReceivedNeighborRequests(Long userId) {
		return neighborRequests.findByReceiver(new UserId(userId)).stream().map(this::toResponse).toList();
	}

	@Override
	public List<NeighborResponse> getNeighbors(Long userId) {
		UserId user = new UserId(userId);
		Map<Long, NeighborResponse> neighborsById = new LinkedHashMap<>();
		neighborships.findByParticipant(user)
			.forEach(neighborship -> {
				UserId neighbor = neighborship.counterpartOf(user);
				neighborsById.putIfAbsent(neighbor.value(), toNeighborResponse(neighborship, user, neighbor));
			});
		return List.copyOf(neighborsById.values());
	}

	@Override
	public void removeNeighborship(RemoveNeighborshipCommand command) {
		UserId user = new UserId(command.userId());
		UserId neighbor = new UserId(command.neighborId());
		Neighborship.validateUsers(user, neighbor);
		boolean exists = neighborships.findByParticipant(user).stream()
			.anyMatch(neighborship -> neighborship.counterpartOf(user).equals(neighbor));
		if (!exists) {
			throw new SocialNotFoundException("Neighborship not found");
		}
		neighborships.deleteBetween(user, neighbor);
	}

	@Override
	public BlockResponse blockUser(BlockUserCommand command) {
		UserId blocker = new UserId(command.blockerId());
		UserId blocked = new UserId(command.blockedId());
		if (blocks.existsBetween(blocker, blocked)) {
			throw new SocialApplicationException("User is already blocked");
		}
		return createBlock(blocker, blocked);
	}

	@Override
	public BlockOperationResult putBlock(BlockUserCommand command) {
		UserId blocker = new UserId(command.blockerId());
		UserId blocked = new UserId(command.blockedId());
		Block.validateUsers(blocker, blocked);
		return blocks.findBetween(blocker, blocked)
			.map(block -> new BlockOperationResult(toResponse(block), false))
			.orElseGet(() -> new BlockOperationResult(createBlock(blocker, blocked), true));
	}

	@Override
	public List<BlockResponse> getBlocks(Long userId) {
		return blocks.findByBlocker(new UserId(userId)).stream().map(this::toResponse).toList();
	}

	@Override
	public PostVisibilityResponse checkPostVisibility(PostVisibilityQuery query) {
		UserId viewer = new UserId(query.viewerId());
		UserId author = new UserId(query.authorId());
		if (viewer.equals(author)) {
			return new PostVisibilityResponse(true, true, false);
		}
		boolean blocked = blocks.existsBetween(viewer, author) || blocks.existsBetween(author, viewer);
		boolean sameNeighborhood = !blocked && neighborhoodMembership.sameNeighborhood(viewer, author);
		boolean activeNeighborship = !blocked && neighborships.existsBetween(viewer, author);
		return new PostVisibilityResponse(sameNeighborhood, activeNeighborship, blocked);
	}

	@Override
	public void unblockUser(UnblockUserCommand command) {
		UserId blocker = new UserId(command.blockerId());
		UserId blocked = new UserId(command.blockedId());
		Block.validateUsers(blocker, blocked);
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
			.map(neighborship -> neighborship.counterpartOf(user))
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

	private NeighborRequest findRequest(Long requestId) {
		return neighborRequests.findById(new NeighborRequestId(requestId))
			.orElseThrow(() -> new SocialApplicationException("Neighbor request not found"));
	}

	private void requireReceiver(NeighborRequest request, Actor actor) {
		if (!request.receiver().value().equals(actor.userId())) {
			throw new SocialAuthorizationException("Access is forbidden");
		}
	}

	private void createNeighborshipIfMissing(UserId user, UserId neighbor) {
		if (!neighborships.exists(user, neighbor)) {
			neighborships.save(Neighborship.create(user, neighbor, LocalDateTime.now()));
		}
	}

	private BlockResponse createBlock(UserId blocker, UserId blocked) {
		neighborships.deleteBetween(blocker, blocked);
		return toResponse(blocks.save(Block.create(blocker, blocked)));
	}

	private NeighborRequestResponse toResponse(NeighborRequest request) {
		Map<UserId, UserDirectory.UserProfile> profiles = profilesById(List.of(request.sender(), request.receiver()));
		return new NeighborRequestResponse(request.id().value(), request.sender().value(), request.receiver().value(),
				request.date(), request.status().name(), toProfileResponse(profiles.get(request.sender())),
				toProfileResponse(profiles.get(request.receiver())));
	}

	private NeighborResponse toNeighborResponse(Neighborship neighborship, UserId user, UserId neighbor) {
		UserDirectory.UserProfile profile = profilesById(List.of(neighbor)).get(neighbor);
		return new NeighborResponse(neighborship.id().value(), user.value(), neighbor.value(), neighborship.date(),
				toProfileResponse(profile));
	}

	private BlockResponse toResponse(Block block) {
		UserDirectory.UserProfile profile = profilesById(List.of(block.blocked())).get(block.blocked());
		return new BlockResponse(block.id().value(), block.blocker().value(), block.blocked().value(),
				toProfileResponse(profile));
	}

	private Map<UserId, UserDirectory.UserProfile> profilesById(Collection<UserId> userIds) {
		Map<UserId, UserDirectory.UserProfile> profiles = userDirectory.findByIds(userIds);
		return profiles == null ? Map.of() : profiles;
	}

	private UserProfileResponse toProfileResponse(UserDirectory.UserProfile profile) {
		return profile == null ? null
				: new UserProfileResponse(profile.id().value(), profile.name(), profile.surname(), profile.username());
	}
}
