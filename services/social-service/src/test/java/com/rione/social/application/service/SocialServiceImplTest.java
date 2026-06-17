package com.rione.social.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import com.rione.social.application.port.in.SocialService;
import com.rione.social.application.port.in.SocialService.BlockUserCommand;
import com.rione.social.application.port.in.SocialService.NeighborRequestResponse;
import com.rione.social.application.port.in.SocialService.SendNeighborRequestCommand;
import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.BlockId;
import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.NeighborRequestId;
import com.rione.social.domain.model.Neighborship;
import com.rione.social.domain.model.NeighborshipId;
import com.rione.social.domain.model.RequestStatus;
import com.rione.social.domain.model.UserId;

class SocialServiceImplTest {

	private final FakeNeighborRequestRepository neighborRequests = new FakeNeighborRequestRepository();
	private final FakeNeighborshipRepository neighborships = new FakeNeighborshipRepository();
	private final FakeBlockRepository blocks = new FakeBlockRepository();
	private final SocialService socialService = new SocialServiceImpl(neighborRequests, neighborships, blocks);

	@Test
	void acceptsNeighborRequestAndCreatesReciprocalNeighborships() {
		NeighborRequestResponse request = socialService.sendNeighborRequest(new SendNeighborRequestCommand(1L, 2L));

		NeighborRequestResponse accepted = socialService.acceptNeighborRequest(request.id());

		assertEquals(RequestStatus.ACCEPTED.name(), accepted.status());
		assertEquals(1, socialService.getNeighborships(1L).size());
		assertEquals(1, socialService.getNeighborships(2L).size());
	}

	@Test
	void rejectsDuplicatePendingNeighborRequest() {
		socialService.sendNeighborRequest(new SendNeighborRequestCommand(1L, 2L));

		assertThrows(SocialApplicationException.class,
				() -> socialService.sendNeighborRequest(new SendNeighborRequestCommand(2L, 1L)));
	}

	@Test
	void blocksUserAndRemovesExistingNeighborships() {
		NeighborRequestResponse request = socialService.sendNeighborRequest(new SendNeighborRequestCommand(1L, 2L));
		socialService.acceptNeighborRequest(request.id());

		socialService.blockUser(new BlockUserCommand(1L, 2L));

		assertEquals(0, socialService.getNeighborships(1L).size());
		assertThrows(SocialApplicationException.class,
				() -> socialService.sendNeighborRequest(new SendNeighborRequestCommand(2L, 1L)));
	}

	private static class FakeNeighborRequestRepository implements NeighborRequestRepository {

		private final AtomicLong sequence = new AtomicLong();
		private final List<NeighborRequest> requests = new ArrayList<>();

		@Override
		public NeighborRequestId nextIdentity() {
			return new NeighborRequestId(sequence.incrementAndGet());
		}

		@Override
		public NeighborRequest save(NeighborRequest request) {
			requests.removeIf(saved -> saved.id().equals(request.id()));
			requests.add(request);
			return request;
		}

		@Override
		public Optional<NeighborRequest> findById(NeighborRequestId id) {
			return requests.stream().filter(request -> request.id().equals(id)).findFirst();
		}

		@Override
		public boolean existsPendingBetween(UserId sender, UserId receiver) {
			return requests.stream()
				.anyMatch(request -> request.status() == RequestStatus.PENDING
						&& (request.sender().equals(sender) && request.receiver().equals(receiver)
								|| request.sender().equals(receiver) && request.receiver().equals(sender)));
		}
	}

	private static class FakeNeighborshipRepository implements NeighborshipRepository {

		private final AtomicLong sequence = new AtomicLong();
		private final List<Neighborship> neighborships = new ArrayList<>();

		@Override
		public NeighborshipId nextIdentity() {
			return new NeighborshipId(sequence.incrementAndGet());
		}

		@Override
		public Neighborship save(Neighborship neighborship) {
			neighborships.add(neighborship);
			return neighborship;
		}

		@Override
		public List<Neighborship> findByFollower(UserId follower) {
			return neighborships.stream()
				.filter(neighborship -> neighborship.follower().equals(follower))
				.toList();
		}

		@Override
		public boolean exists(UserId follower, UserId followed) {
			return neighborships.stream()
				.anyMatch(neighborship -> neighborship.follower().equals(follower)
						&& neighborship.followed().equals(followed));
		}

		@Override
		public void deleteBetween(UserId firstUser, UserId secondUser) {
			neighborships.removeIf(neighborship -> neighborship.follower().equals(firstUser)
					&& neighborship.followed().equals(secondUser)
					|| neighborship.follower().equals(secondUser) && neighborship.followed().equals(firstUser));
		}
	}

	private static class FakeBlockRepository implements BlockRepository {

		private final AtomicLong sequence = new AtomicLong();
		private final List<Block> blocks = new ArrayList<>();

		@Override
		public BlockId nextIdentity() {
			return new BlockId(sequence.incrementAndGet());
		}

		@Override
		public Block save(Block block) {
			blocks.add(block);
			return block;
		}

		@Override
		public boolean existsBetween(UserId blocker, UserId blocked) {
			return blocks.stream()
				.anyMatch(block -> block.blocker().equals(blocker) && block.blocked().equals(blocked));
		}
	}
}
