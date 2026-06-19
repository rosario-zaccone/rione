package com.rione.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import com.rione.user.application.port.in.UserService.SignUpCommand;
import com.rione.user.application.port.in.UserService.UserResponse;
import com.rione.user.application.port.out.PasswordHasher;
import com.rione.user.application.port.out.UserRepository;
import com.rione.user.application.service.UserServiceImpl;
import com.rione.user.domain.model.Biography;
import com.rione.user.domain.model.BirthDate;
import com.rione.user.domain.model.FullName;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.NeighborhoodId;
import com.rione.user.domain.model.Password;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;
import com.rione.user.domain.model.Username;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserStepDefinitions {

	private UserRepository users;
	private UserServiceImpl service;
	private UserResponse response;
	private Throwable thrown;

	@Before
	public void reset() {
		users = org.mockito.Mockito.mock(UserRepository.class);
		PasswordHasher passwordHasher = org.mockito.Mockito.mock(PasswordHasher.class);
		when(passwordHasher.hash("correct horse battery staple")).thenReturn("hashed-password");
		when(users.save(any(User.class))).thenReturn(user(1L, "grace", "grace@rione.test"));
		service = new UserServiceImpl(users, passwordHasher);
		response = null;
		thrown = null;
	}

	@Given("a registered user has mail {string}")
	public void aRegisteredUserHasMail(String mail) {
		when(users.existsByMail(new Mail(mail))).thenReturn(true);
	}

	@Given("a registered user has username {string}")
	public void aRegisteredUserHasUsername(String username) {
		when(users.existsByUsername(new Username(username))).thenReturn(true);
	}

	@When("a resident signs up with username {string} and mail {string}")
	public void aResidentSignsUpWithUsernameAndMail(String username, String mail) {
		when(users.save(any(User.class))).thenReturn(user(1L, username, mail));
		thrown = catchThrowable(() -> response = service.signUp(validSignUp(username, mail)));
	}

	@Then("the user account is created")
	public void theUserAccountIsCreated() {
		assertThat(thrown).isNull();
		assertThat(response.id()).isPositive();
		assertThat(response.admin()).isFalse();
	}

	@Then("the user operation is rejected with {string}")
	public void theUserOperationIsRejectedWith(String message) {
		assertThat(thrown).hasMessage(message);
	}

	private static SignUpCommand validSignUp(String username, String mail) {
		return new SignUpCommand("Ada", "Lovelace", username, mail, "correct horse battery staple", 10L,
				LocalDateTime.of(1990, 1, 1, 0, 0), "I enjoy helping neighbors solve local problems.");
	}

	private static User user(Long id, String username, String mail) {
		return User.restore(new UserId(id), new FullName("Ada", "Lovelace"), new Username(username), new Mail(mail),
				new NeighborhoodId(10L), new BirthDate(LocalDateTime.of(1990, 1, 1, 0, 0)),
				new Biography("I enjoy helping neighbors solve local problems."), new Password("hashed-password"), false);
	}
}
