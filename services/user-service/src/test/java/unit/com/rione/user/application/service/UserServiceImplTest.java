package com.rione.user.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import com.rione.user.application.port.in.UserService;
import com.rione.user.application.port.in.UserService.LogInCommand;
import com.rione.user.application.port.in.UserService.SearchNeighboursQuery;
import com.rione.user.application.port.in.UserService.SignUpCommand;
import com.rione.user.application.port.in.UserService.UpdateProfileCommand;
import com.rione.user.application.port.in.UserService.UserResponse;
import com.rione.user.application.port.out.UserRepository;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;
import com.rione.user.domain.model.Username;
import com.rione.user.infrastructure.config.Pbkdf2PasswordHasher;

class UserServiceImplTest {

	private final UserService userService = new UserServiceImpl(new FakeUserRepository(), new Pbkdf2PasswordHasher());

	@Test
	void signsUpAndLogsInUser() {
		UserResponse user = signUp("ada", "ada@rione.test");

		UserResponse loggedIn = userService.logIn(new LogInCommand("ada@rione.test", "password123"));

		assertEquals(user.id(), loggedIn.id());
	}

	@Test
	void rejectsDuplicateMail() {
		signUp("ada", "ada@rione.test");

		assertThrows(UserApplicationException.class, () -> signUp("ada2", "ada@rione.test"));
	}

	@Test
	void excludesRequesterFromNeighbourSearch() {
		UserResponse ada = signUp("ada", "ada@rione.test");
		UserResponse grace = signUp("grace", "grace@rione.test");

		List<UserResponse> neighbours = userService.searchNeighbours(new SearchNeighboursQuery(ada.id(), "", 1L));

		assertEquals(1, neighbours.size());
		assertEquals(grace.id(), neighbours.getFirst().id());
	}

	@Test
	void updatesProfileInformation() {
		UserResponse ada = signUp("ada", "ada@rione.test");

		UserResponse updated = userService.updateProfile(new UpdateProfileCommand(ada.id(), "Ada", "Byron", "ada.byron",
				2L, "San Donato", "Bologna", "Italy", LocalDateTime.of(1991, 2, 3, 0, 0), "updated"));

		assertEquals("Byron", updated.surname());
		assertEquals("ada.byron", updated.username());
		assertEquals(2L, updated.neighborhoodId());
		assertEquals("updated", updated.bio());
		assertEquals(LocalDateTime.of(1991, 2, 3, 0, 0), updated.birthDate());
	}

	private UserResponse signUp(String username, String mail) {
		return userService.signUp(new SignUpCommand("Ada", "Lovelace", username, mail, "password123", 1L, "Centro",
				"Bologna", "Italy", LocalDateTime.of(1990, 1, 1, 0, 0), "hello"));
	}

	private static class FakeUserRepository implements UserRepository {

		private final AtomicLong sequence = new AtomicLong();
		private final List<User> users = new ArrayList<>();

		@Override
		public UserId nextIdentity() {
			return new UserId(sequence.incrementAndGet());
		}

		@Override
		public User save(User user) {
			users.removeIf(saved -> saved.id().equals(user.id()));
			users.add(user);
			return user;
		}

		@Override
		public Optional<User> findById(UserId userId) {
			return users.stream().filter(user -> user.id().equals(userId)).findFirst();
		}

		@Override
		public Optional<User> findByMail(Mail mail) {
			return users.stream().filter(user -> user.mail().equals(mail)).findFirst();
		}

		@Override
		public boolean existsByMail(Mail mail) {
			return users.stream().anyMatch(user -> user.mail().equals(mail));
		}

		@Override
		public boolean existsByUsername(Username username) {
			return users.stream().anyMatch(user -> user.username().equals(username));
		}

		@Override
		public List<User> search(String query, Long neighborhoodId) {
			return users.stream()
				.filter(user -> neighborhoodId == null || user.neighborhood().id().value().equals(neighborhoodId))
				.toList();
		}
	}
}
