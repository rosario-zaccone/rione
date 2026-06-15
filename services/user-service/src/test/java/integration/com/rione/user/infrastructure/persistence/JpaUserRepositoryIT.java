package com.rione.user.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.rione.user.PostgresIntegrationSupport;
import com.rione.user.application.port.out.UserRepository;
import com.rione.user.domain.model.Biography;
import com.rione.user.domain.model.FullName;
import com.rione.user.domain.model.Location;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.Neighborhood;
import com.rione.user.domain.model.NeighborhoodId;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.Username;

@SpringBootTest
class JpaUserRepositoryIT extends PostgresIntegrationSupport {

	@Autowired
	private UserRepository userRepository;

	@Test
	void persistsAndLoadsUserInPostgres() {
		User ada = user("ada.persistence", "ada.persistence@rione.test");

		userRepository.save(ada);

		User loaded = userRepository.findByMail(new Mail("ada.persistence@rione.test")).orElseThrow();
		assertEquals("ada.persistence", loaded.username().value());
		assertEquals("Centro", loaded.neighborhood().name());
	}

	private User user(String username, String mail) {
		return User.register(userRepository.nextIdentity(), new FullName("Ada", "Lovelace"), new Username(username),
				new Mail(mail), new Neighborhood(new NeighborhoodId(1L), "Centro", new Location("Bologna", "Italy")),
				LocalDateTime.of(1990, 1, 1, 0, 0), new Biography("hello"), "hash");
	}
}
