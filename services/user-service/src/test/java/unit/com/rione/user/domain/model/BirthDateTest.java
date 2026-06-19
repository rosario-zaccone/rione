package com.rione.user.domain.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class BirthDateTest {

	@Test
	void rejectsFutureBirthDate() {
		assertThatThrownBy(() -> new BirthDate(LocalDateTime.now().plusDays(1)))
			.isInstanceOf(DomainException.class)
			.hasMessage("Birth date must be in the past");
	}
}
