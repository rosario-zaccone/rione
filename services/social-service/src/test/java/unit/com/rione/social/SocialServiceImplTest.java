package com.rione.social;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.rione.social.application.port.in.SocialService.Actor;
import com.rione.social.application.port.in.SocialService.BlockUserCommand;
import com.rione.social.application.port.in.SocialService.RemoveNeighborshipCommand;
import com.rione.social.application.port.in.SocialService.NeighborhoodChangedCommand;
import com.rione.social.application.port.in.SocialService.NeighborRequestResponse;
import com.rione.social.application.port.in.SocialService.PostVisibilityQuery;
import com.rione.social.application.port.in.SocialService.SendNeighborRequestCommand;
import com.rione.social.application.port.in.SocialService.UnblockUserCommand;
import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.application.port.out.NeighborhoodMembership;
import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.application.port.out.SocialEventPublisher;
import com.rione.social.application.port.out.UserDirectory;
import com.rione.social.application.port.out.UserDirectory.UserProfile;
import com.rione.social.application.service.SocialApplicationException;
import com.rione.social.application.service.SocialNotFoundException;
import com.rione.social.application.service.SocialServiceImpl;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.BlockId;
import com.rione.social.domain.model.DomainException;
import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.NeighborRequestId;
import com.rione.social.domain.model.Neighborship;
import com.rione.social.domain.model.NeighborshipId;
import com.rione.social.domain.model.RequestStatus;
import com.rione.social.domain.model.UserId;

class SocialServiceImplTest {

	private NeighborRequestRepository requests;
	private NeighborshipRepository neighborships;
	private BlockRepository blocks;
	private NeighborhoodMembership neighborhoods;
	private UserDirectory userDirectory;
	private SocialEventPublisher socialEvents;
	private SocialServiceImpl service;

	@BeforeEach
	void setUp() {
		requests = mock(NeighborRequestRepository.class);
		neighborships = mock(NeighborshipRepository.class);
		blocks = mock(BlockRepository.class);
		neighborhoods = mock(NeighborhoodMembership.class);
		userDirectory = mock(UserDirectory.class);
		socialEvents = mock(SocialEventPublisher.class);
		service = new SocialServiceImpl(requests, neighborships, blocks, neighborhoods, userDirectory, socialEvents);
		givenSameNeighborhood(1L, 2L);
		when(requests.save(any())).thenAnswer(invocation -> withId((NeighborRequest) invocation.getArgument(0)));
		when(neighborships.save(any())).thenAnswer(invocation -> withId((Neighborship) invocation.getArgument(0)));
		when(blocks.save(any())).thenAnswer(invocation -> withId((Block) invocation.getArgument(0)));
	}

	@Test
	void searchesVisibleUsersByProfileText() {
		when(userDirectory.searchInNeighborhood(new UserId(1L), "ada")).thenReturn(List.of(
				new UserProfile(new UserId(1L), "Current", "User", "current"),
				new UserProfile(new UserId(2L), "Ada", "Lovelace", "ada"),
				new UserProfile(new UserId(3L), "Ada", "Blocked", "blocked")));
		when(blocks.existsBetween(new UserId(3L), new UserId(1L))).thenReturn(true);

		assertThat(service.searchUsers(new com.rione.social.application.port.in.SocialService.SearchUsersQuery(1L, " ada ")))
			.extracting(response -> response.id(), response -> response.name(), response -> response.surname(),
					response -> response.username())
			.containsExactly(org.assertj.core.groups.Tuple.tuple(2L, "Ada", "Lovelace", "ada"));
	}

	@Test
	void blankSearchReturnsEmptyWithoutCallingDirectory() {
		assertThat(service.searchUsers(new com.rione.social.application.port.in.SocialService.SearchUsersQuery(1L, "  ")))
			.isEmpty();
		verify(userDirectory, never()).searchInNeighborhood(any(), any());
	}

	@Test
	void checksPostVisibilityRelationshipForSameNeighborhoodNeighborshipAndBlocks() {
		when(neighborships.existsBetween(new UserId(1L), new UserId(2L))).thenReturn(true);

		assertThat(service.checkPostVisibility(new PostVisibilityQuery(1L, 2L)))
			.extracting(response -> response.sameNeighborhood(), response -> response.activeNeighborship(),
					response -> response.blocked())
			.containsExactly(true, true, false);

		when(blocks.existsBetween(new UserId(2L), new UserId(1L))).thenReturn(true);

		assertThat(service.checkPostVisibility(new PostVisibilityQuery(1L, 2L)))
			.extracting(response -> response.sameNeighborhood(), response -> response.activeNeighborship(),
					response -> response.blocked())
			.containsExactly(false, false, true);
	}

	@Test
	void rejectsNeighborRequestAcrossNeighborhoods() {
		givenDifferentNeighborhoods(1L, 2L);

		assertThatThrownBy(() -> service.sendNeighborRequest(new SendNeighborRequestCommand(1L, 2L)))
			.isInstanceOf(SocialApplicationException.class)
			.hasMessage("Users must belong to the same neighborhood");

		verify(requests, never()).save(any());
	}

	@Test
	void rejectsSelfNeighborRequest() {
		assertThatThrownBy(() -> service.sendNeighborRequest(new SendNeighborRequestCommand(1L, 1L)))
			.isInstanceOf(DomainException.class)
			.hasMessage("Neighbor request sender and receiver must be different users");

		verify(requests, never()).save(any());
	}

	@Test
	void rejectsDuplicatePendingNeighborRequest() {
		when(requests.existsPendingBetween(new UserId(1L), new UserId(2L))).thenReturn(true);

		assertThatThrownBy(() -> service.sendNeighborRequest(new SendNeighborRequestCommand(1L, 2L)))
			.isInstanceOf(SocialApplicationException.class)
			.hasMessage("A pending neighbor request already exists between these users");

		verify(requests, never()).save(any());
	}

	@Test
	void listsSentNeighborRequestsForUser() {
		when(requests.findBySender(new UserId(1L))).thenReturn(List.of(
				NeighborRequest.restore(new NeighborRequestId(1L), new UserId(1L), new UserId(2L), LocalDateTime.now(),
						RequestStatus.PENDING)));

		assertThat(service.getSentNeighborRequests(1L))
			.extracting(response -> response.senderId(), response -> response.receiverId(), response -> response.status())
			.containsExactly(org.assertj.core.groups.Tuple.tuple(1L, 2L, "PENDING"));
	}

	@Test
	void sendingNeighborRequestPublishesRequestReceivedEvent() {
		NeighborRequestResponse response = service.sendNeighborRequest(new SendNeighborRequestCommand(1L, 2L));

		ArgumentCaptor<NeighborRequest> captor = ArgumentCaptor.forClass(NeighborRequest.class);
		verify(socialEvents).publishNeighborRequestReceived(captor.capture());
		assertThat(captor.getValue())
			.extracting(request -> request.id().value(), request -> request.sender().value(),
					request -> request.receiver().value(), request -> request.status())
			.containsExactly(response.id(), 1L, 2L, RequestStatus.PENDING);
	}

	@Test
	void listsReceivedNeighborRequestsForUser() {
		when(requests.findByReceiver(new UserId(1L))).thenReturn(List.of(
				NeighborRequest.restore(new NeighborRequestId(2L), new UserId(3L), new UserId(1L), LocalDateTime.now(),
						RequestStatus.PENDING)));

		assertThat(service.getReceivedNeighborRequests(1L))
			.extracting(response -> response.senderId(), response -> response.receiverId(), response -> response.status())
			.containsExactly(org.assertj.core.groups.Tuple.tuple(3L, 1L, "PENDING"));
	}

	@Test
	void acceptingRequestCreatesReciprocalNeighborship() {
		NeighborRequest request = NeighborRequest.restore(new NeighborRequestId(10L), new UserId(1L), new UserId(2L),
				LocalDateTime.of(2026, 1, 1, 0, 0), RequestStatus.PENDING);
		when(requests.findById(new NeighborRequestId(10L))).thenReturn(Optional.of(request));
		when(neighborships.exists(any(), any())).thenReturn(false);

		service.acceptNeighborRequest(10L, new Actor(2L));

		verify(requests).save(request);
		ArgumentCaptor<Neighborship> captor = ArgumentCaptor.forClass(Neighborship.class);
		verify(neighborships, org.mockito.Mockito.times(2)).save(captor.capture());
		org.assertj.core.api.Assertions.assertThat(captor.getAllValues())
			.extracting(neighborship -> neighborship.follower().value(), neighborship -> neighborship.followed().value())
			.containsExactlyInAnyOrder(org.assertj.core.groups.Tuple.tuple(1L, 2L),
					org.assertj.core.groups.Tuple.tuple(2L, 1L));
		ArgumentCaptor<NeighborRequest> eventCaptor = ArgumentCaptor.forClass(NeighborRequest.class);
		verify(socialEvents).publishNeighborRequestAccepted(eventCaptor.capture());
		assertThat(eventCaptor.getValue())
			.extracting(event -> event.id().value(), event -> event.sender().value(), event -> event.receiver().value(),
					event -> event.status())
			.containsExactly(10L, 1L, 2L, RequestStatus.ACCEPTED);
	}

	@Test
	void rejectsAcceptingRequestWhenUsersNoLongerShareNeighborhood() {
		NeighborRequest request = NeighborRequest.restore(new NeighborRequestId(10L), new UserId(1L), new UserId(2L),
				LocalDateTime.of(2026, 1, 1, 0, 0), RequestStatus.PENDING);
		when(requests.findById(new NeighborRequestId(10L))).thenReturn(Optional.of(request));
		givenDifferentNeighborhoods(1L, 2L);

		assertThatThrownBy(() -> service.acceptNeighborRequest(10L, new Actor(2L)))
			.isInstanceOf(SocialApplicationException.class)
			.hasMessage("Users must belong to the same neighborhood");

		verify(requests, never()).save(any());
		verify(neighborships, never()).save(any());
	}

	@Test
	void blockingRemovesExistingNeighborshipAndPreventsFutureRequests() {
		service.blockUser(new BlockUserCommand(1L, 2L));

		verify(neighborships).deleteBetween(new UserId(1L), new UserId(2L));
		verify(blocks).save(any(Block.class));

		when(blocks.existsBetween(new UserId(2L), new UserId(1L))).thenReturn(true);
		assertThatThrownBy(() -> service.sendNeighborRequest(new SendNeighborRequestCommand(2L, 1L)))
			.isInstanceOf(SocialApplicationException.class)
			.hasMessage("Neighbor request cannot be sent between blocked users");
	}

	@Test
	void rejectsAcceptingRequestWhenActorIsNotReceiver() {
		NeighborRequest request = NeighborRequest.restore(new NeighborRequestId(10L), new UserId(1L), new UserId(2L),
				LocalDateTime.of(2026, 1, 1, 0, 0), RequestStatus.PENDING);
		when(requests.findById(new NeighborRequestId(10L))).thenReturn(Optional.of(request));

		assertThatThrownBy(() -> service.acceptNeighborRequest(10L, new Actor(1L)))
			.isInstanceOf(com.rione.social.application.service.SocialAuthorizationException.class)
			.hasMessage("Access is forbidden");

		verify(requests, never()).save(any());
		verify(neighborships, never()).save(any());
	}

	@Test
	void rejectsAdminWhoIsNotTheRequestReceiver() {
		NeighborRequest request = NeighborRequest.restore(new NeighborRequestId(10L), new UserId(1L), new UserId(2L),
				LocalDateTime.of(2026, 1, 1, 0, 0), RequestStatus.PENDING);
		when(requests.findById(new NeighborRequestId(10L))).thenReturn(Optional.of(request));
		assertThatThrownBy(() -> service.acceptNeighborRequest(10L, new Actor(99L)))
			.isInstanceOf(com.rione.social.application.service.SocialAuthorizationException.class)
			.hasMessage("Access is forbidden");

		verify(requests, never()).save(any());
	}

	@Test
	void unblockingRemovesBlockWithoutRestoringNeighborship() {
		service.unblockUser(new UnblockUserCommand(1L, 2L));

		verify(blocks).deleteBetween(new UserId(1L), new UserId(2L));
		verify(neighborships, never()).save(any());
	}

	@Test
	void putBlockCreatesBlockWhenMissing() {
		when(blocks.findBetween(new UserId(1L), new UserId(2L))).thenReturn(Optional.empty());

		var result = service.putBlock(new BlockUserCommand(1L, 2L));

		assertThat(result.created()).isTrue();
		assertThat(result.block()).extracting(response -> response.blockerId(), response -> response.blockedId())
			.containsExactly(1L, 2L);
		verify(blocks).save(any(Block.class));
		verify(neighborships).deleteBetween(new UserId(1L), new UserId(2L));
	}

	@Test
	void putBlockReturnsExistingBlockWithoutCreatingDuplicate() {
		Block existing = Block.restore(new BlockId(7L), new UserId(1L), new UserId(2L));
		when(blocks.findBetween(new UserId(1L), new UserId(2L))).thenReturn(Optional.of(existing));

		var result = service.putBlock(new BlockUserCommand(1L, 2L));

		assertThat(result.created()).isFalse();
		assertThat(result.block().id()).isEqualTo(7L);
		verify(blocks, never()).save(any());
		verify(neighborships, never()).deleteBetween(any(), any());
	}

	@Test
	void removesExistingNeighborshipRegardlessOfStoredDirection() {
		when(neighborships.findByParticipant(new UserId(1L))).thenReturn(List.of(
				Neighborship.restore(new NeighborshipId(1L), new UserId(2L), new UserId(1L), LocalDateTime.now())));

		service.removeNeighborship(new RemoveNeighborshipCommand(1L, 2L));

		verify(neighborships).deleteBetween(new UserId(1L), new UserId(2L));
	}

	@Test
	void rejectsRemovingMissingNeighborship() {
		when(neighborships.findByParticipant(new UserId(1L))).thenReturn(List.of());

		assertThatThrownBy(() -> service.removeNeighborship(new RemoveNeighborshipCommand(1L, 2L)))
			.isInstanceOf(SocialNotFoundException.class)
			.hasMessage("Neighborship not found");

		verify(neighborships, never()).deleteBetween(any(), any());
	}

	@Test
	void rejectsRemovingNeighborshipWithSelf() {
		assertThatThrownBy(() -> service.removeNeighborship(new RemoveNeighborshipCommand(1L, 1L)))
			.isInstanceOf(DomainException.class)
			.hasMessage("Neighborship users must be different");
	}

	@Test
	void rejectsUnblockingSelf() {
		assertThatThrownBy(() -> service.unblockUser(new UnblockUserCommand(1L, 1L)))
			.isInstanceOf(DomainException.class)
			.hasMessage("Blocker and blocked user must be different users");
	}

	@Test
	void listsBlocksCreatedByUser() {
		when(blocks.findByBlocker(new UserId(1L))).thenReturn(List.of(Block.restore(new BlockId(1L), new UserId(1L),
				new UserId(2L)), Block.restore(new BlockId(2L), new UserId(1L), new UserId(3L))));

		assertThat(service.getBlocks(1L))
			.extracting(response -> response.blockerId(), response -> response.blockedId())
			.containsExactly(org.assertj.core.groups.Tuple.tuple(1L, 2L),
					org.assertj.core.groups.Tuple.tuple(1L, 3L));
	}

	@Test
	void listsUniqueNeighborsForUserFromAnyDirection() {
		when(neighborships.findByParticipant(new UserId(1L))).thenReturn(List.of(
				Neighborship.restore(new NeighborshipId(1L), new UserId(1L), new UserId(2L), LocalDateTime.now()),
				Neighborship.restore(new NeighborshipId(2L), new UserId(2L), new UserId(1L), LocalDateTime.now()),
				Neighborship.restore(new NeighborshipId(3L), new UserId(3L), new UserId(1L), LocalDateTime.now())));

		assertThat(service.getNeighbors(1L))
			.extracting(response -> response.userId(), response -> response.neighborId())
			.containsExactly(org.assertj.core.groups.Tuple.tuple(1L, 2L),
					org.assertj.core.groups.Tuple.tuple(1L, 3L));
	}

	@Test
	void reconcileAfterNeighborhoodChangeRemovesInvalidConnectionsAndRequests() {
		givenDifferentNeighborhoods(1L, 2L);
		givenSameNeighborhood(1L, 3L);
		when(neighborships.findByParticipant(new UserId(1L))).thenReturn(List.of(
				Neighborship.restore(new NeighborshipId(1L), new UserId(1L), new UserId(2L), LocalDateTime.now()),
				Neighborship.restore(new NeighborshipId(2L), new UserId(1L), new UserId(3L), LocalDateTime.now())));
		when(requests.findPendingInvolving(new UserId(1L))).thenReturn(List.of(
				NeighborRequest.restore(new NeighborRequestId(1L), new UserId(1L), new UserId(2L), LocalDateTime.now(),
						RequestStatus.PENDING),
				NeighborRequest.restore(new NeighborRequestId(2L), new UserId(1L), new UserId(3L), LocalDateTime.now(),
						RequestStatus.PENDING)));

		service.reconcileRelationshipsAfterNeighborhoodChange(new NeighborhoodChangedCommand(1L));

		verify(neighborships).deleteBetween(new UserId(1L), new UserId(2L));
		verify(neighborships, never()).deleteBetween(new UserId(1L), new UserId(3L));
		verify(requests).delete(new NeighborRequestId(1L));
		verify(requests, never()).delete(new NeighborRequestId(2L));
	}

	private void givenSameNeighborhood(Long firstUser, Long secondUser) {
		when(neighborhoods.sameNeighborhood(new UserId(firstUser), new UserId(secondUser))).thenReturn(true);
		when(neighborhoods.sameNeighborhood(new UserId(secondUser), new UserId(firstUser))).thenReturn(true);
	}

	private void givenDifferentNeighborhoods(Long firstUser, Long secondUser) {
		when(neighborhoods.sameNeighborhood(new UserId(firstUser), new UserId(secondUser))).thenReturn(false);
		when(neighborhoods.sameNeighborhood(new UserId(secondUser), new UserId(firstUser))).thenReturn(false);
	}

	private NeighborRequest withId(NeighborRequest request) {
		NeighborRequestId id = request.id() == null ? new NeighborRequestId(1L) : request.id();
		return NeighborRequest.restore(id, request.sender(), request.receiver(), request.date(), request.status());
	}

	private Neighborship withId(Neighborship neighborship) {
		NeighborshipId id = neighborship.id() == null ? new NeighborshipId(1L) : neighborship.id();
		return Neighborship.restore(id, neighborship.follower(), neighborship.followed(), neighborship.date());
	}

	private Block withId(Block block) {
		BlockId id = block.id() == null ? new BlockId(1L) : block.id();
		return Block.restore(id, block.blocker(), block.blocked());
	}
}
