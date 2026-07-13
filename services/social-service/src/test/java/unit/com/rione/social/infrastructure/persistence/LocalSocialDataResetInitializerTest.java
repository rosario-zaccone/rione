package com.rione.social.infrastructure.persistence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.Neighborship;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

class LocalSocialDataResetInitializerTest {

	@Test
	void resetsAndReseedsSocialDataOnEveryStartup() {
		EntityManager entityManager = mock(EntityManager.class);
		Query truncateQuery = mock(Query.class);
		when(entityManager.createNativeQuery("TRUNCATE TABLE neighbor_requests, neighborships, blocks RESTART IDENTITY"))
			.thenReturn(truncateQuery);
		NeighborshipRepository neighborships = mock(NeighborshipRepository.class);
		NeighborRequestRepository neighborRequests = mock(NeighborRequestRepository.class);
		BlockRepository blocks = mock(BlockRepository.class);

		new LocalSocialDataResetInitializer(entityManager, neighborships, neighborRequests, blocks)
			.run(mock(ApplicationArguments.class));

		verify(truncateQuery, times(1)).executeUpdate();
		verify(neighborships, times(19)).save(any(Neighborship.class));
		verify(neighborRequests, times(4)).save(any(NeighborRequest.class));
		verify(blocks, times(1)).save(any(Block.class));
	}
}
