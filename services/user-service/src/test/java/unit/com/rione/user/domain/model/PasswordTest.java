package com.rione.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PasswordTest {

	@Test
	void acceptsPasswordHash() {
		assertEquals("hash", new Password("hash").hash());
	}

	@Test
	void rejectsMissingPasswordHash() {
		assertThrows(DomainException.class, () -> new Password(null));
		assertThrows(DomainException.class, () -> new Password(" "));
	}
}
