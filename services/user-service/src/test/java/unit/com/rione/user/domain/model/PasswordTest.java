package com.rione.user.domain.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PasswordTest {

	@Test
	void rejectsBlankPasswordHash() {
		assertThatThrownBy(() -> new Password(" "))
			.isInstanceOf(DomainException.class)
			.hasMessage("Password hash is required");
	}
}
