package com.rione.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BiographyTest {

	@Test
	void normalizesBiography() {
		assertEquals("This is a useful profile bio", new Biography("  This is a useful profile bio  ").info());
	}

	@Test
	void rejectsBlankBiography() {
		assertThrows(DomainException.class, () -> new Biography(null));
		assertThrows(DomainException.class, () -> new Biography(""));
		assertThrows(DomainException.class, () -> new Biography("   "));
	}

	@Test
	void rejectsTooShortBiography() {
		String tooShort = "a".repeat(Biography.MIN_LENGTH - 1);

		assertThrows(DomainException.class, () -> new Biography(tooShort));
	}

	@Test
	void rejectsTooLongBiography() {
		String tooLong = "a".repeat(Biography.MAX_LENGTH + 1);

		assertThrows(DomainException.class, () -> new Biography(tooLong));
	}
}
