package com.rione.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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

@Testcontainers
@SpringBootTest
@Transactional
class JpaUserRepositoryIT {

	@Container
	static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
		registry.add("spring.sql.init.mode", () -> "never");
	}

	@Autowired
	private UserRepository users;

	@Test
	void savesAndLoadsUserByMail() {
		User saved = users.save(user("ada", "ada@rione.test", 10L));

		assertThat(saved.id()).isNotNull();
		assertThat(users.findByMail(new Mail("ada@rione.test")))
			.get()
			.extracting(user -> user.username().value(), user -> user.neighborhoodId().value())
			.containsExactly("ada", 10L);
	}

	@Test
	void detectsUsersInNeighborhoods() {
		users.save(user("ada", "ada@rione.test", 10L));

		assertThat(users.existsByNeighborhoodIdIn(List.of(new NeighborhoodId(10L), new NeighborhoodId(20L)))).isTrue();
		assertThat(users.existsByNeighborhoodIdIn(List.of(new NeighborhoodId(30L)))).isFalse();
	}

	@Test
	void updatesExistingUserWhenIdIsPresent() {
		User saved = users.save(user("ada", "ada@rione.test", 10L));

		users.save(User.restore(saved.id(), new FullName("Augusta", "King"), new Username("augusta"), saved.mail(),
				new NeighborhoodId(20L), saved.birthDate(),
				new Biography("I coordinate local reading groups and courtyard projects."), saved.password(), false));

		assertThat(users.findById(new UserId(saved.id().value())))
			.get()
			.extracting(user -> user.fullName().displayName(), user -> user.username().value(),
					user -> user.neighborhoodId().value())
			.containsExactly("Augusta King", "augusta", 20L);
	}

	private static User user(String username, String mail, Long neighborhoodId) {
		return User.register(new FullName("Ada", "Lovelace"), new Username(username), new Mail(mail),
				new NeighborhoodId(neighborhoodId), new BirthDate(LocalDateTime.of(1990, 1, 1, 0, 0)),
				new Biography("I enjoy helping neighbors solve local problems."), new Password("hashed-password"));
	}
}
