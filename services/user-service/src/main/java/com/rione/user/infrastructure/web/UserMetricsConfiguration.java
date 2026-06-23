package com.rione.user.infrastructure.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rione.user.application.port.out.UserRepository;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;

@Configuration
class UserMetricsConfiguration {

	@Bean
	MeterBinder registeredUsersMeterBinder(UserRepository users) {
		return registry -> Gauge.builder("rione_registered_users", users, UserRepository::countRegisteredUsers)
			.description("Current number of registered users")
			.baseUnit("users")
			.register(registry);
	}
}
