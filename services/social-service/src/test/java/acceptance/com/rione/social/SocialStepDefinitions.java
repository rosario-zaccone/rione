package com.rione.social;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.rione.social.application.port.in.SocialService.BlockUserCommand;
import com.rione.social.application.port.in.SocialService.BlockResponse;
import com.rione.social.application.port.in.SocialService.Actor;
import com.rione.social.application.port.in.SocialService.NeighborhoodChangedCommand;
import com.rione.social.application.port.in.SocialService.NeighborRequestResponse;
import com.rione.social.application.port.in.SocialService.RemoveNeighborshipCommand;
import com.rione.social.application.port.in.SocialService.SendNeighborRequestCommand;
import com.rione.social.application.port.in.SocialService.UnblockUserCommand;
import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.application.port.out.NeighborhoodMembership;
import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.application.service.SocialServiceImpl;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.BlockId;
import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.NeighborRequestId;
import com.rione.social.domain.model.Neighborship;
import com.rione.social.domain.model.NeighborshipId;
import com.rione.social.domain.model.RequestStatus;
import com.rione.social.domain.model.UserId;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SocialStepDefinitions {

	private final AtomicLong requestSequence = new AtomicLong();
	private final AtomicLong neighborshipSequence = new AtomicLong();
	private final AtomicLong blockSequence = new AtomicLong();
	private final List<NeighborRequest> savedRequests = new ArrayList<>();
	private final List<Neighborship> savedNeighborships = new ArrayList<>();
	private final List<Block> savedBlocks = new ArrayList<>();

	private SocialServiceImpl service;
	private NeighborRequestRepository requests;
	private NeighborshipRepository neighborships;
	private BlockRepository blocks;
	private NeighborhoodMembership neighborhoods;
	private NeighborRequestResponse requestResponse;
	private List<NeighborRequestResponse> neighborRequestResponses;
	private List<BlockResponse> blockResponses;
	private Throwable thrown;

	@Before
	public void reset() {
		requestSequence.set(0);
		neighborshipSequence.set(0);
		blockSequence.set(0);
		savedRequests.clear();
		savedNeighborships.clear();
		savedBlocks.clear();
		requests = mock(NeighborRequestRepository.class);
		neighborships = mock(NeighborshipRepository.class);
		blocks = mock(BlockRepository.class);
		neighborhoods = mock(NeighborhoodMembership.class);
		stubRequestRepository();
		stubNeighborshipRepository();
		stubBlockRepository();
		service = new SocialServiceImpl(requests, neighborships, blocks, neighborhoods);
		requestResponse = null;
		neighborRequestResponses = List.of();
		blockResponses = List.of();
		thrown = null;
	}

	@Given("users {long} and {long} belong to the same neighborhood")
	public void usersBelongToTheSameNeighborhood(Long firstUser, Long secondUser) {
		givenSameNeighborhood(firstUser, secondUser);
		assertThat(firstUser).isNotEqualTo(secondUser);
	}

	@Given("users {long} and {long} belong to different neighborhoods")
	public void usersBelongToDifferentNeighborhoods(Long firstUser, Long secondUser) {
		givenDifferentNeighborhoods(firstUser, secondUser);
	}

	@Given("a pending neighbour request exists from user {long} to user {long}")
	public void aPendingNeighbourRequestExists(Long senderId, Long receiverId) {
		givenSameNeighborhood(senderId, receiverId);
		requestResponse = service.sendNeighborRequest(new SendNeighborRequestCommand(senderId, receiverId));
	}

	@Given("users {long} and {long} are neighbours")
	public void usersAreNeighbours(Long firstUser, Long secondUser) {
		givenSameNeighborhood(firstUser, secondUser);
		saveNeighborship(Neighborship.create(new UserId(firstUser), new UserId(secondUser), LocalDateTime.now()));
		saveNeighborship(Neighborship.create(new UserId(secondUser), new UserId(firstUser), LocalDateTime.now()));
	}

	@Given("user {long} has blocked user {long}")
	public void userHasBlockedUser(Long blockerId, Long blockedId) {
		givenSameNeighborhood(blockerId, blockedId);
		service.blockUser(new BlockUserCommand(blockerId, blockedId));
	}

	@Given("user {long} changes neighborhood")
	public void userChangesNeighborhood(Long userId) {
		givenDifferentNeighborhoods(userId, 2L);
		givenSameNeighborhood(userId, 3L);
		saveNeighborship(Neighborship.create(new UserId(userId), new UserId(2L), LocalDateTime.now()));
		saveNeighborship(Neighborship.create(new UserId(2L), new UserId(userId), LocalDateTime.now()));
		saveNeighborship(Neighborship.create(new UserId(userId), new UserId(3L), LocalDateTime.now()));
		saveNeighborship(Neighborship.create(new UserId(3L), new UserId(userId), LocalDateTime.now()));
		saveRequest(NeighborRequest.create(new UserId(userId), new UserId(2L), LocalDateTime.now()));
		saveRequest(NeighborRequest.create(new UserId(userId), new UserId(3L), LocalDateTime.now()));
	}

	@When("user {long} sends a neighbour request to user {long}")
	public void userSendsANeighbourRequestToUser(Long senderId, Long receiverId) {
		thrown = catchThrowable(
				() -> requestResponse = service.sendNeighborRequest(new SendNeighborRequestCommand(senderId, receiverId)));
	}

	@When("the request is accepted")
	public void theRequestIsAccepted() {
		requestResponse = service.acceptNeighborRequest(requestResponse.id(), new Actor(requestResponse.receiverId()));
	}

	@When("user {long} blocks user {long}")
	public void userBlocksUser(Long blockerId, Long blockedId) {
		thrown = catchThrowable(() -> service.blockUser(new BlockUserCommand(blockerId, blockedId)));
	}

	@When("user {long} unblocks user {long}")
	public void userUnblocksUser(Long blockerId, Long blockedId) {
		service.unblockUser(new UnblockUserCommand(blockerId, blockedId));
	}

	@When("user {long} puts a block for user {long}")
	public void userPutsABlockForUser(Long blockerId, Long blockedId) {
		thrown = catchThrowable(() -> service.putBlock(new BlockUserCommand(blockerId, blockedId)));
	}

	@When("user {long} removes the neighborship with user {long}")
	public void userRemovesTheNeighborshipWithUser(Long userId, Long neighborId) {
		thrown = catchThrowable(() -> service.removeNeighborship(new RemoveNeighborshipCommand(userId, neighborId)));
	}

	@When("social relationships are reconciled for user {long}")
	public void socialRelationshipsAreReconciledForUser(Long userId) {
		service.reconcileRelationshipsAfterNeighborhoodChange(new NeighborhoodChangedCommand(userId));
	}

	@When("user {long} lists blocked users")
	public void userListsBlockedUsers(Long userId) {
		blockResponses = service.getBlocks(userId);
	}

	@When("user {long} lists sent neighbour requests")
	public void userListsSentNeighbourRequests(Long userId) {
		neighborRequestResponses = service.getSentNeighborRequests(userId);
	}

	@When("user {long} lists received neighbour requests")
	public void userListsReceivedNeighbourRequests(Long userId) {
		neighborRequestResponses = service.getReceivedNeighborRequests(userId);
	}

	@Then("the neighbour request is created")
	public void theNeighbourRequestIsCreated() {
		assertThat(thrown).isNull();
		assertThat(requestResponse.id()).isPositive();
		assertThat(requestResponse.status()).isEqualTo("PENDING");
	}

	@Then("the social operation is rejected with {string}")
	public void theSocialOperationIsRejectedWith(String message) {
		assertThat(thrown).hasMessage(message);
	}

	@Then("a reciprocal neighborship exists between user {long} and user {long}")
	public void aReciprocalNeighborshipExistsBetweenUserAndUser(Long firstUser, Long secondUser) {
		assertThat(neighborshipExists(firstUser, secondUser)).isTrue();
		assertThat(neighborshipExists(secondUser, firstUser)).isTrue();
	}

	@Then("the neighborship between user {long} and user {long} is removed")
	public void theNeighborshipBetweenUserAndUserIsRemoved(Long firstUser, Long secondUser) {
		assertThat(neighborshipExists(firstUser, secondUser)).isFalse();
		assertThat(neighborshipExists(secondUser, firstUser)).isFalse();
	}

	@Then("exactly one block exists from user {long} to user {long}")
	public void exactlyOneBlockExistsFromUserToUser(Long blockerId, Long blockedId) {
		assertThat(savedBlocks).filteredOn(block -> block.blocker().equals(new UserId(blockerId))
				&& block.blocked().equals(new UserId(blockedId)))
			.hasSize(1);
	}

	@Then("user {long} is no longer blocked by user {long}")
	public void userIsNoLongerBlockedByUser(Long blockedId, Long blockerId) {
		assertThat(blockExists(blockerId, blockedId)).isFalse();
	}

	@Then("the blocked users list contains user {long}")
	public void theBlockedUsersListContainsUser(Long blockedId) {
		assertThat(blockResponses).extracting(BlockResponse::blockedId).contains(blockedId);
	}

	@Then("the blocked users list does not contain user {long}")
	public void theBlockedUsersListDoesNotContainUser(Long blockedId) {
		assertThat(blockResponses).extracting(BlockResponse::blockedId).doesNotContain(blockedId);
	}

	@Then("the sent neighbour request list contains a request to user {long}")
	public void theSentNeighbourRequestListContainsARequestToUser(Long receiverId) {
		assertThat(neighborRequestResponses).extracting(NeighborRequestResponse::receiverId).contains(receiverId);
	}

	@Then("the sent neighbour request list does not contain a request from user {long}")
	public void theSentNeighbourRequestListDoesNotContainARequestFromUser(Long senderId) {
		assertThat(neighborRequestResponses).extracting(NeighborRequestResponse::senderId).doesNotContain(senderId);
	}

	@Then("the received neighbour request list contains a request from user {long}")
	public void theReceivedNeighbourRequestListContainsARequestFromUser(Long senderId) {
		assertThat(neighborRequestResponses).extracting(NeighborRequestResponse::senderId).contains(senderId);
	}

	@Then("the received neighbour request list does not contain a request to user {long}")
	public void theReceivedNeighbourRequestListDoesNotContainARequestToUser(Long receiverId) {
		assertThat(neighborRequestResponses).extracting(NeighborRequestResponse::receiverId).doesNotContain(receiverId);
	}

	@Then("the neighborship between user {long} and user {long} is not restored")
	public void theNeighborshipBetweenUserAndUserIsNotRestored(Long firstUser, Long secondUser) {
		assertThat(neighborshipExists(firstUser, secondUser)).isFalse();
		assertThat(neighborshipExists(secondUser, firstUser)).isFalse();
	}

	@Then("neighbour connections outside the new neighborhood are removed")
	public void neighbourConnectionsOutsideTheNewNeighborhoodAreRemoved() {
		assertThat(neighborshipExists(1L, 2L)).isFalse();
		assertThat(neighborshipExists(1L, 3L)).isTrue();
	}

	@Then("pending neighbour requests outside the new neighborhood are removed")
	public void pendingNeighbourRequestsOutsideTheNewNeighborhoodAreRemoved() {
		assertThat(pendingRequestExists(1L, 2L)).isFalse();
		assertThat(pendingRequestExists(1L, 3L)).isTrue();
	}

	private void stubRequestRepository() {
		when(requests.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
			NeighborRequest saved = saveRequest(invocation.getArgument(0));
			return saved;
		});
		when(requests.findById(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
			NeighborRequestId id = invocation.getArgument(0);
			return savedRequests.stream().filter(request -> request.id().equals(id)).findFirst();
		});
		when(requests.findBySender(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
			UserId sender = invocation.getArgument(0);
			return savedRequests.stream().filter(request -> request.sender().equals(sender)).toList();
		});
		when(requests.findByReceiver(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
			UserId receiver = invocation.getArgument(0);
			return savedRequests.stream().filter(request -> request.receiver().equals(receiver)).toList();
		});
		when(requests.existsPendingBetween(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
			.thenAnswer(invocation -> pendingRequestExists((UserId) invocation.getArgument(0),
					(UserId) invocation.getArgument(1)));
		when(requests.findPendingInvolving(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
			UserId userId = invocation.getArgument(0);
			return savedRequests.stream()
				.filter(request -> request.status() == RequestStatus.PENDING && request.involves(userId))
				.toList();
		});
		org.mockito.Mockito.doAnswer(invocation -> {
			NeighborRequestId id = invocation.getArgument(0);
			savedRequests.removeIf(request -> request.id().equals(id));
			return null;
		}).when(requests).delete(org.mockito.ArgumentMatchers.any());
	}

	private void stubNeighborshipRepository() {
		when(neighborships.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
			Neighborship saved = saveNeighborship(invocation.getArgument(0));
			return saved;
		});
		when(neighborships.findByParticipant(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
			UserId userId = invocation.getArgument(0);
			return savedNeighborships.stream().filter(neighborship -> neighborship.involves(userId)).toList();
		});
		when(neighborships.exists(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
			.thenAnswer(invocation -> neighborshipExists((UserId) invocation.getArgument(0),
					(UserId) invocation.getArgument(1)));
		when(neighborships.existsBetween(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				UserId firstUser = invocation.getArgument(0);
				UserId secondUser = invocation.getArgument(1);
				return neighborshipExists(firstUser, secondUser) || neighborshipExists(secondUser, firstUser);
			});
		org.mockito.Mockito.doAnswer(invocation -> {
			UserId firstUser = invocation.getArgument(0);
			UserId secondUser = invocation.getArgument(1);
			savedNeighborships.removeIf(neighborship -> sameDirectedNeighborship(neighborship, firstUser, secondUser)
					|| sameDirectedNeighborship(neighborship, secondUser, firstUser));
			return null;
		}).when(neighborships)
			.deleteBetween(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
	}

	private void stubBlockRepository() {
		when(blocks.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
			Block block = invocation.getArgument(0);
			Block saved = block.id() == null
					? Block.restore(new BlockId(blockSequence.incrementAndGet()), block.blocker(), block.blocked())
					: block;
			savedBlocks.removeIf(existing -> existing.id().equals(saved.id()));
			savedBlocks.add(saved);
			return saved;
		});
		when(blocks.findByBlocker(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
			UserId blocker = invocation.getArgument(0);
			return savedBlocks.stream().filter(block -> block.blocker().equals(blocker)).toList();
		});
		when(blocks.findBetween(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
			.thenAnswer(invocation -> {
				UserId blocker = invocation.getArgument(0);
				UserId blocked = invocation.getArgument(1);
				return savedBlocks.stream()
					.filter(block -> block.blocker().equals(blocker) && block.blocked().equals(blocked))
					.findFirst();
			});
		when(blocks.existsBetween(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
			.thenAnswer(invocation -> blockExists((UserId) invocation.getArgument(0), (UserId) invocation.getArgument(1)));
		org.mockito.Mockito.doAnswer(invocation -> {
			UserId blocker = invocation.getArgument(0);
			UserId blocked = invocation.getArgument(1);
			savedBlocks.removeIf(block -> block.blocker().equals(blocker) && block.blocked().equals(blocked));
			return null;
		}).when(blocks).deleteBetween(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
	}

	private NeighborRequest saveRequest(NeighborRequest request) {
		NeighborRequest saved = request.id() == null
				? NeighborRequest.restore(new NeighborRequestId(requestSequence.incrementAndGet()), request.sender(),
						request.receiver(), request.date(), request.status())
				: request;
		savedRequests.removeIf(existing -> existing.id().equals(saved.id()));
		savedRequests.add(saved);
		return saved;
	}

	private Neighborship saveNeighborship(Neighborship neighborship) {
		Neighborship saved = neighborship.id() == null
				? Neighborship.restore(new NeighborshipId(neighborshipSequence.incrementAndGet()),
						neighborship.follower(), neighborship.followed(), neighborship.date())
				: neighborship;
		savedNeighborships.removeIf(existing -> existing.id().equals(saved.id()));
		savedNeighborships.add(saved);
		return saved;
	}

	private boolean pendingRequestExists(Long sender, Long receiver) {
		return pendingRequestExists(new UserId(sender), new UserId(receiver));
	}

	private boolean pendingRequestExists(UserId sender, UserId receiver) {
		return savedRequests.stream()
			.anyMatch(request -> request.status() == RequestStatus.PENDING
					&& (request.sender().equals(sender) && request.receiver().equals(receiver)
							|| request.sender().equals(receiver) && request.receiver().equals(sender)));
	}

	private boolean neighborshipExists(Long follower, Long followed) {
		return neighborshipExists(new UserId(follower), new UserId(followed));
	}

	private boolean neighborshipExists(UserId follower, UserId followed) {
		return savedNeighborships.stream()
			.anyMatch(neighborship -> sameDirectedNeighborship(neighborship, follower, followed));
	}

	private boolean sameDirectedNeighborship(Neighborship neighborship, UserId follower, UserId followed) {
		return neighborship.follower().equals(follower) && neighborship.followed().equals(followed);
	}

	private boolean blockExists(Long blocker, Long blocked) {
		return blockExists(new UserId(blocker), new UserId(blocked));
	}

	private boolean blockExists(UserId blocker, UserId blocked) {
		return savedBlocks.stream()
			.anyMatch(block -> block.blocker().equals(blocker) && block.blocked().equals(blocked));
	}

	private void givenSameNeighborhood(Long firstUser, Long secondUser) {
		when(neighborhoods.sameNeighborhood(new UserId(firstUser), new UserId(secondUser))).thenReturn(true);
		when(neighborhoods.sameNeighborhood(new UserId(secondUser), new UserId(firstUser))).thenReturn(true);
	}

	private void givenDifferentNeighborhoods(Long firstUser, Long secondUser) {
		when(neighborhoods.sameNeighborhood(new UserId(firstUser), new UserId(secondUser))).thenReturn(false);
		when(neighborhoods.sameNeighborhood(new UserId(secondUser), new UserId(firstUser))).thenReturn(false);
	}
}
