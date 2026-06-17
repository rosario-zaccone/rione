package com.rione.social.domain.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BlockTest {

	@Test
	void rejectsSelfBlock() {
		UserId user = new UserId(1L);

		assertThrows(DomainException.class, () -> Block.create(new BlockId(1L), user, user));
	}
}
