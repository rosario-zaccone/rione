package com.rione.social.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.Neighborship;
import com.rione.social.domain.model.UserId;

import jakarta.persistence.EntityManager;

@Component
@Profile("local")
class LocalSocialDataResetInitializer implements ApplicationRunner {

	private final EntityManager entityManager;
	private final NeighborshipRepository neighborships;
	private final NeighborRequestRepository neighborRequests;
	private final BlockRepository blocks;

	LocalSocialDataResetInitializer(EntityManager entityManager, NeighborshipRepository neighborships,
			NeighborRequestRepository neighborRequests, BlockRepository blocks) {
		this.entityManager = entityManager;
		this.neighborships = neighborships;
		this.neighborRequests = neighborRequests;
		this.blocks = blocks;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		entityManager
			.createNativeQuery("TRUNCATE TABLE neighbor_requests, neighborships, blocks RESTART IDENTITY")
			.executeUpdate();

		for (SeedNeighborship seed : seedNeighborships()) {
			neighborships.save(Neighborship.create(new UserId(seed.follower()), new UserId(seed.followed()),
					seed.date()));
		}
		for (SeedRequest seed : seedPendingRequests()) {
			neighborRequests.save(NeighborRequest.create(new UserId(seed.sender()), new UserId(seed.receiver()),
					seed.date()));
		}
		for (SeedBlock seed : seedBlocks()) {
			blocks.save(Block.create(new UserId(seed.blocker()), new UserId(seed.blocked())));
		}
	}

	private static List<SeedNeighborship> seedNeighborships() {
		return List.of(
				// Saragozza neighborhood
				new SeedNeighborship(1L, 2L, LocalDateTime.of(2026, 5, 2, 10, 0)),
				new SeedNeighborship(1L, 3L, LocalDateTime.of(2026, 5, 3, 11, 0)),
				new SeedNeighborship(2L, 4L, LocalDateTime.of(2026, 5, 4, 9, 30)),
				new SeedNeighborship(3L, 5L, LocalDateTime.of(2026, 5, 5, 14, 0)),
				new SeedNeighborship(4L, 6L, LocalDateTime.of(2026, 5, 6, 16, 15)),
				new SeedNeighborship(5L, 7L, LocalDateTime.of(2026, 5, 7, 8, 45)),
				new SeedNeighborship(6L, 8L, LocalDateTime.of(2026, 5, 8, 12, 30)),
				new SeedNeighborship(8L, 10L, LocalDateTime.of(2026, 5, 9, 17, 0)),
				// Navile neighborhood
				new SeedNeighborship(11L, 12L, LocalDateTime.of(2026, 5, 10, 10, 0)),
				new SeedNeighborship(12L, 13L, LocalDateTime.of(2026, 5, 11, 11, 30)),
				new SeedNeighborship(13L, 14L, LocalDateTime.of(2026, 5, 12, 13, 0)),
				new SeedNeighborship(14L, 15L, LocalDateTime.of(2026, 5, 13, 15, 45)),
				new SeedNeighborship(16L, 17L, LocalDateTime.of(2026, 5, 14, 9, 0)),
				new SeedNeighborship(17L, 18L, LocalDateTime.of(2026, 5, 15, 10, 30)),
				new SeedNeighborship(19L, 20L, LocalDateTime.of(2026, 5, 16, 18, 0)),
				new SeedNeighborship(22L, 23L, LocalDateTime.of(2026, 5, 17, 11, 15)),
				new SeedNeighborship(23L, 24L, LocalDateTime.of(2026, 5, 18, 16, 30)),
				// cross-neighborhood connections
				new SeedNeighborship(10L, 11L, LocalDateTime.of(2026, 5, 19, 12, 0)),
				new SeedNeighborship(5L, 20L, LocalDateTime.of(2026, 5, 20, 9, 45)));
	}

	private static List<SeedRequest> seedPendingRequests() {
		return List.of(new SeedRequest(3L, 9L, LocalDateTime.of(2026, 6, 25, 10, 0)),
				new SeedRequest(7L, 10L, LocalDateTime.of(2026, 6, 26, 15, 30)),
				new SeedRequest(14L, 21L, LocalDateTime.of(2026, 6, 27, 8, 15)),
				new SeedRequest(18L, 24L, LocalDateTime.of(2026, 6, 28, 17, 45)));
	}

	private static List<SeedBlock> seedBlocks() {
		return List.of(new SeedBlock(9L, 4L));
	}

	private record SeedNeighborship(Long follower, Long followed, LocalDateTime date) {
	}

	private record SeedRequest(Long sender, Long receiver, LocalDateTime date) {
	}

	private record SeedBlock(Long blocker, Long blocked) {
	}
}
