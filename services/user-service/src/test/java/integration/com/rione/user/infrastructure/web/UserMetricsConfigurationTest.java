package com.rione.user.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rione.user.application.port.out.UserRepository;

import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import org.junit.jupiter.api.Test;

class UserMetricsConfigurationTest {

	@Test
	void exportsDashboardMetricNamesWithoutBaseUnitSuffixes() {
		UserRepository users = org.mockito.Mockito.mock(UserRepository.class);
		ActiveUserTracker activeUserTracker = org.mockito.Mockito.mock(ActiveUserTracker.class);
		when(users.countRegisteredUsers()).thenReturn(20L);
		when(users.countRegisteredUsersSince(any())).thenReturn(2L);
		when(activeUserTracker.activeUsers()).thenReturn(5L);
		PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

		UserMetricsConfiguration configuration = new UserMetricsConfiguration();
		configuration.registeredUsersMeterBinder(users).bindTo(registry);
		configuration.activeUsersMeterBinder(activeUserTracker).bindTo(registry);

		String scrape = registry.scrape();
		assertThat(scrape).contains("rione_registered_users 20.0");
		assertThat(scrape).contains("rione_new_users_today 2.0");
		assertThat(scrape).contains("rione_active_users_last_5_minutes 5.0");
		assertThat(scrape).doesNotContain("rione_registered_users_users");
		assertThat(scrape).doesNotContain("rione_active_users_last_5_minutes_users");
	}
}
