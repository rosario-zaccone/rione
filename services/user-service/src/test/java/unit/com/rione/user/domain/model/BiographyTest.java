package com.rione.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class BiographyTest {

	@Test
	void trimsAcceptedBiography() {
		Biography biography = new Biography("  I coordinate local mutual aid every weekend.  ");

		assertThat(biography.info()).isEqualTo("I coordinate local mutual aid every weekend.");
	}

	@Test
	void rejectsBiographyShorterThanMinimumLength() {
		assertThatThrownBy(() -> new Biography("Too short"))
			.isInstanceOf(DomainException.class)
			.hasMessage("Biography must contain at least 20 characters");
	}
}
