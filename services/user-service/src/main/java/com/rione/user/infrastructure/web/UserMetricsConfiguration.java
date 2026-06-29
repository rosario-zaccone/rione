package com.rione.user.infrastructure.web;

import java.time.LocalDate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rione.user.application.port.out.UserRepository;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;

@Configuration
class UserMetricsConfiguration {

	@Bean
	MeterBinder registeredUsersMeterBinder(UserRepository users) {
		return registry -> {
			Gauge.builder("rione_registered_users", users, UserRepository::countRegisteredUsers)
				.description("Current number of registered users")
				.baseUnit("users")
				.register(registry);
			Gauge.builder("rione_new_users_today", users,
					repository -> repository.countRegisteredUsersSince(LocalDate.now()))
				.description("Users registered since the start of the current service day")
				.baseUnit("users")
				.register(registry);
		};
	}

	@Bean
	MeterBinder activeUsersMeterBinder(ActiveUserTracker activeUserTracker) {
		return registry -> Gauge.builder("rione_active_users_last_5_minutes", activeUserTracker,
				ActiveUserTracker::activeUsers)
			.description("Authenticated users active in the last five minutes")
			.baseUnit("users")
			.register(registry);
	}
}
