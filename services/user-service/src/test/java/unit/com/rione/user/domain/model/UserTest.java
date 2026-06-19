package com.rione.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class UserTest {

	@Test
	void updateProfileChangesEditableProfileFields() {
		User user = User.restore(new UserId(1L), new FullName("Ada", "Lovelace"), new Username("ada"),
				new Mail("ada@rione.test"), new NeighborhoodId(10L),
				new BirthDate(LocalDateTime.of(1990, 1, 1, 0, 0)),
				new Biography("I enjoy helping neighbors solve local problems."), new Password("hashed-password"), false);

		user.updateProfile(new FullName("Augusta", "King"), new Username("augusta"), new NeighborhoodId(20L),
				new BirthDate(LocalDateTime.of(1991, 1, 1, 0, 0)),
				new Biography("I coordinate local reading groups and courtyard projects."));

		assertThat(user.fullName().displayName()).isEqualTo("Augusta King");
		assertThat(user.username().value()).isEqualTo("augusta");
		assertThat(user.neighborhoodId().value()).isEqualTo(20L);
		assertThat(user.mail().mail()).isEqualTo("ada@rione.test");
	}
}
