package com.rione.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class BirthDateTest {

	@Test
	void acceptsPastBirthDate() {
		LocalDateTime birthDate = LocalDateTime.of(1990, 1, 1, 0, 0);

		assertEquals(birthDate, new BirthDate(birthDate).value());
	}

	@Test
	void rejectsMissingOrFutureBirthDate() {
		assertThrows(DomainException.class, () -> new BirthDate(null));
		assertThrows(DomainException.class, () -> new BirthDate(LocalDateTime.now().plusDays(1)));
	}
}
