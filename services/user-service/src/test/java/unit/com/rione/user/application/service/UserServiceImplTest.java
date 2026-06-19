package com.rione.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rione.user.application.port.in.UserService.LogInCommand;
import com.rione.user.application.port.in.UserService.SignUpCommand;
import com.rione.user.application.port.in.UserService.UpdateProfileCommand;
import com.rione.user.application.port.in.UserService.UserResponse;
import com.rione.user.application.port.out.PasswordHasher;
import com.rione.user.application.port.out.UserRepository;
import com.rione.user.domain.model.Biography;
import com.rione.user.domain.model.BirthDate;
import com.rione.user.domain.model.FullName;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.NeighborhoodId;
import com.rione.user.domain.model.Password;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;
import com.rione.user.domain.model.Username;

class UserServiceImplTest {

	private UserRepository users;
	private PasswordHasher passwordHasher;
	private UserServiceImpl service;

	@BeforeEach
	void setUp() {
		users = org.mockito.Mockito.mock(UserRepository.class);
		passwordHasher = org.mockito.Mockito.mock(PasswordHasher.class);
		service = new UserServiceImpl(users, passwordHasher);
		when(passwordHasher.hash("secret-password")).thenReturn("hashed-secret-password");
	}

	@Test
	void createsUserWhenSignUpCommandIsValid() {
		when(users.save(any(User.class))).thenReturn(user(1L, "ada", "ada@rione.test", "hashed-secret-password"));

		UserResponse response = service.signUp(validSignUp("ada", "ada@rione.test"));

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.username()).isEqualTo("ada");
		assertThat(response.mail()).isEqualTo("ada@rione.test");
		assertThat(response.admin()).isFalse();
		verify(users).save(any(User.class));
	}

	@Test
	void rejectsDuplicateMail() {
		when(users.existsByMail(new Mail("ada@rione.test"))).thenReturn(true);

		assertThatThrownBy(() -> service.signUp(validSignUp("grace", "ada@rione.test")))
			.isInstanceOf(UserApplicationException.class)
			.hasMessage("Mail is already registered");

		verify(users, never()).save(any(User.class));
	}

	@Test
	void rejectsDuplicateUsername() {
		when(users.existsByUsername(new Username("ada"))).thenReturn(true);

		assertThatThrownBy(() -> service.signUp(validSignUp("ada", "other@rione.test")))
			.isInstanceOf(UserApplicationException.class)
			.hasMessage("Username is already registered");

		verify(users, never()).save(any(User.class));
	}

	@Test
	void returnsUserWhenLoginCredentialsMatch() {
		when(users.findByMail(new Mail("ada@rione.test")))
			.thenReturn(Optional.of(user(1L, "ada", "ada@rione.test", "hashed-secret-password")));
		when(passwordHasher.matches("secret-password", "hashed-secret-password")).thenReturn(true);

		UserResponse response = service.logIn(new LogInCommand("ada@rione.test", "secret-password"));

		assertThat(response.username()).isEqualTo("ada");
	}

	@Test
	void rejectsLoginWithWrongPassword() {
		when(users.findByMail(new Mail("ada@rione.test")))
			.thenReturn(Optional.of(user(1L, "ada", "ada@rione.test", "hashed-secret-password")));
		when(passwordHasher.matches("wrong-password", "hashed-secret-password")).thenReturn(false);

		assertThatThrownBy(() -> service.logIn(new LogInCommand("ada@rione.test", "wrong-password")))
			.isInstanceOf(UserApplicationException.class)
			.hasMessage("Invalid mail or password");
	}

	@Test
	void updatesProfileWhenUsernameRemainsUnique() {
		User existing = user(1L, "ada", "ada@rione.test", "hashed-secret-password");
		when(users.findById(new UserId(1L))).thenReturn(Optional.of(existing));
		when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserResponse updated = service.updateProfile(new UpdateProfileCommand(1L, "Augusta", "King", "augusta", 20L,
				LocalDateTime.of(1991, 1, 1, 0, 0),
				"I coordinate local reading groups and shared courtyard projects."));

		assertThat(updated.name()).isEqualTo("Augusta");
		assertThat(updated.surname()).isEqualTo("King");
		assertThat(updated.username()).isEqualTo("augusta");
		assertThat(updated.neighborhoodId()).isEqualTo(20L);
	}

	@Test
	void rejectsProfileUpdateWithAnotherUsersUsername() {
		when(users.findById(new UserId(1L)))
			.thenReturn(Optional.of(user(1L, "ada", "ada@rione.test", "hashed-secret-password")));
		when(users.existsByUsername(new Username("grace"))).thenReturn(true);

		assertThatThrownBy(() -> service.updateProfile(new UpdateProfileCommand(1L, "Ada", "Lovelace", "grace", 10L,
				LocalDateTime.of(1990, 1, 1, 0, 0), "I enjoy helping neighbors solve local problems.")))
			.isInstanceOf(UserApplicationException.class)
			.hasMessage("Username is already registered");

		verify(users, never()).save(any(User.class));
	}

	private static SignUpCommand validSignUp(String username, String mail) {
		return new SignUpCommand("Ada", "Lovelace", username, mail, "secret-password", 10L,
				LocalDateTime.of(1990, 1, 1, 0, 0), "I enjoy helping neighbors solve local problems.");
	}

	private static User user(Long id, String username, String mail, String passwordHash) {
		return User.restore(new UserId(id), new FullName("Ada", "Lovelace"), new Username(username), new Mail(mail),
				new NeighborhoodId(10L), new BirthDate(LocalDateTime.of(1990, 1, 1, 0, 0)),
				new Biography("I enjoy helping neighbors solve local problems."), new Password(passwordHash), false);
	}
}
