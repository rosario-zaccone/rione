package com.rione.post.infrastructure.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.rione.post.domain.model.UserId;
import com.rione.post.infrastructure.config.JwtService;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

/**
 * Exercises the real Resilience4j circuit breaker state machine behind {@link SocialPostVisibilityProxy},
 * simulating the downstream social-service being down.
 */
class SocialPostVisibilityProxyCircuitBreakerTest {

	private static final String BASE_URL = "http://social-service.test";
	private static final String CIRCUIT_BREAKER_NAME = "social-service";
	private static final String REQUEST_URL = BASE_URL + "/internal/social/post-visibility?viewerId=1&authorId=2";

	private CircuitBreakerRegistry circuitBreakerRegistry;
	private MockRestServiceServer server;
	private SocialPostVisibilityProxy proxy;

	@BeforeEach
	void setUp() {
		CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
			.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
			.slidingWindowSize(4)
			.minimumNumberOfCalls(4)
			.failureRateThreshold(50)
			.waitDurationInOpenState(Duration.ofMillis(300))
			.permittedNumberOfCallsInHalfOpenState(2)
			.automaticTransitionFromOpenToHalfOpenEnabled(true)
			.build();
		circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
		io.github.resilience4j.circuitbreaker.CircuitBreaker delegate = circuitBreakerRegistry
			.circuitBreaker(CIRCUIT_BREAKER_NAME);

		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		JwtService jwtService = mock(JwtService.class);
		when(jwtService.createServiceToken()).thenReturn("service-token");

		@SuppressWarnings("unchecked")
		CircuitBreakerFactory<Object, ?> circuitBreakerFactory = mock(CircuitBreakerFactory.class);
		when(circuitBreakerFactory.create(CIRCUIT_BREAKER_NAME))
			.thenReturn(new SpringCircuitBreakerAdapter(delegate));
		proxy = new SocialPostVisibilityProxy(builder, BASE_URL, jwtService, circuitBreakerFactory);
	}

	@Test
	void opensCircuitAfterRepeatedDownstreamFailuresThenFailsFastWithoutCallingTheDownService() {
		simulateDownServiceResponses(4);

		for (int i = 0; i < 4; i++) {
			assertThatThrownBy(() -> proxy.visibilityBetween(new UserId(1L), new UserId(2L)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Post visibility could not be resolved");
		}

		assertThat(circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).getState()).isEqualTo(State.OPEN);

		assertThatThrownBy(() -> proxy.visibilityBetween(new UserId(1L), new UserId(2L)))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("Post visibility could not be resolved")
			.hasCauseInstanceOf(CallNotPermittedException.class);

		server.verify();
	}

	@Test
	void recoversToClosedOnceTheServiceComesBackUp() {
		simulateDownServiceResponses(4);
		server.expect(requestTo(REQUEST_URL))
			.andRespond(withSuccess("{\"sameNeighborhood\":true,\"activeNeighborship\":true,\"blocked\":false}",
					MediaType.APPLICATION_JSON));
		server.expect(requestTo(REQUEST_URL))
			.andRespond(withSuccess("{\"sameNeighborhood\":true,\"activeNeighborship\":true,\"blocked\":false}",
					MediaType.APPLICATION_JSON));

		for (int i = 0; i < 4; i++) {
			assertThatThrownBy(() -> proxy.visibilityBetween(new UserId(1L), new UserId(2L)))
				.isInstanceOf(IllegalStateException.class);
		}
		assertThat(circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).getState()).isEqualTo(State.OPEN);

		await().atMost(Duration.ofSeconds(2))
			.untilAsserted(() -> assertThat(circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).getState())
				.isNotEqualTo(State.OPEN));

		assertThat(proxy.visibilityBetween(new UserId(1L), new UserId(2L)).sameNeighborhood()).isTrue();
		assertThat(proxy.visibilityBetween(new UserId(1L), new UserId(2L)).sameNeighborhood()).isTrue();

		assertThat(circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).getState()).isEqualTo(State.CLOSED);
		server.verify();
	}

	private void simulateDownServiceResponses(int count) {
		for (int i = 0; i < count; i++) {
			server.expect(requestTo(REQUEST_URL)).andRespond(request -> {
				throw new IOException("Connection refused: social-service is down");
			});
		}
	}

	/**
	 * Minimal, faithful stand-in for {@code Resilience4JCircuitBreaker}: delegates straight to the
	 * real resilience4j {@code CircuitBreaker.executeSupplier}. Used instead of the Spring Cloud
	 * wrapper only because that wrapper's constructor requires the (unrelated) resilience4j-bulkhead
	 * module, which this service does not depend on.
	 */
	private record SpringCircuitBreakerAdapter(io.github.resilience4j.circuitbreaker.CircuitBreaker delegate)
			implements org.springframework.cloud.client.circuitbreaker.CircuitBreaker {

		@Override
		public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
			try {
				return delegate.executeSupplier(toRun);
			}
			catch (Throwable throwable) {
				return fallback.apply(throwable);
			}
		}
	}
}
