package com.rione.user.domain.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class UserTest {

	@Test
	void updatesProfile() {
		User user = user(1);

		user.updateProfile(new FullName("Grace", "Hopper"), new Username("grace"),
				new Neighborhood(new NeighborhoodId(2L), "San Donato", new Location("Bologna", "Italy")),
				LocalDateTime.of(1991, 2, 3, 0, 0), new Biography("updated"));

		assertEquals("Grace", user.fullName().name());
		assertEquals("grace", user.username().value());
		assertEquals(2L, user.neighborhood().id().value());
		assertEquals("updated", user.bio().info());
	}

	@Test
	void rejectsInvalidMail() {
		assertThrows(DomainException.class, () -> new Mail("not-a-mail"));
	}

	private static User user(long id) {
		return User.register(new UserId(id), new FullName("Ada", "Lovelace"), new Username("ada" + id),
				new Mail("ada" + id + "@rione.test"),
				new Neighborhood(new NeighborhoodId(1L), "Centro", new Location("Bologna", "Italy")),
				LocalDateTime.of(1990, 1, 1, 0, 0), new Biography(""), "hash");
	}
}
