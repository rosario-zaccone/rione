package com.rione.social.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.NeighborRequestId;
import com.rione.social.domain.model.Neighborship;
import com.rione.social.domain.model.RequestStatus;
import com.rione.social.domain.model.UserId;

@Testcontainers
@SpringBootTest
@Transactional
class JpaSocialRepositoriesIT {

	@Container
	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
		registry.add("spring.sql.init.mode", () -> "never");
	}

	@Autowired
	private NeighborRequestRepository neighborRequests;

	@Autowired
	private NeighborshipRepository neighborships;

	@Autowired
	private BlockRepository blocks;

	@Test
	void savesAndLoadsNeighborRequest() {
		NeighborRequest saved = neighborRequests.save(NeighborRequest.create(new UserId(1L), new UserId(2L),
				LocalDateTime.of(2026, 1, 1, 0, 0)));

		assertThat(saved.id()).isNotNull();
		assertThat(neighborRequests.findById(new NeighborRequestId(saved.id().value())))
			.get()
			.extracting(NeighborRequest::status, request -> request.sender().value(), request -> request.receiver().value())
			.containsExactly(RequestStatus.PENDING, 1L, 2L);
		assertThat(neighborRequests.existsPendingBetween(new UserId(2L), new UserId(1L))).isTrue();
		assertThat(neighborRequests.findPendingInvolving(new UserId(1L))).hasSize(1);
	}

	@Test
	void savesFindsAndDeletesNeighborshipsBetweenUsers() {
		neighborships.save(Neighborship.create(new UserId(1L), new UserId(2L), LocalDateTime.now()));
		neighborships.save(Neighborship.create(new UserId(2L), new UserId(1L), LocalDateTime.now()));

		assertThat(neighborships.findByFollower(new UserId(1L))).hasSize(1);
		assertThat(neighborships.findByParticipant(new UserId(1L))).hasSize(2);
		assertThat(neighborships.exists(new UserId(1L), new UserId(2L))).isTrue();

		neighborships.deleteBetween(new UserId(1L), new UserId(2L));

		assertThat(neighborships.findByParticipant(new UserId(1L))).isEmpty();
	}

	@Test
	void savesDetectsAndDeletesBlock() {
		Block saved = blocks.save(Block.create(new UserId(1L), new UserId(2L)));

		assertThat(saved.id()).isNotNull();
		assertThat(blocks.existsBetween(new UserId(1L), new UserId(2L))).isTrue();

		blocks.deleteBetween(new UserId(1L), new UserId(2L));

		assertThat(blocks.existsBetween(new UserId(1L), new UserId(2L))).isFalse();
	}
}
