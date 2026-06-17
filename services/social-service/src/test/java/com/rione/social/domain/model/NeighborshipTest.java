package com.rione.social.domain.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class NeighborshipTest {

	@Test
	void rejectsSelfNeighborship() {
		UserId user = new UserId(1L);

		assertThrows(DomainException.class,
				() -> Neighborship.create(new NeighborshipId(1L), user, user, LocalDateTime.now()));
	}
}
