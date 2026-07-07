package com.rione.post.infrastructure.proxy;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rione.post.application.port.out.PostUserDirectory;
import com.rione.post.domain.model.UserId;
import com.rione.post.infrastructure.config.JwtService;

@Component
class UserServiceProfileProxy implements PostUserDirectory {

	private final RestClient restClient;
	private final JwtService jwtService;
	private final CircuitBreaker userServiceCircuitBreaker;

	UserServiceProfileProxy(RestClient.Builder restClientBuilder,
			@Value("${rione.clients.user-service.base-url:http://localhost:8081}") String userServiceBaseUrl,
			JwtService jwtService, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
		this.restClient = restClientBuilder.baseUrl(userServiceBaseUrl).build();
		this.jwtService = jwtService;
		this.userServiceCircuitBreaker = circuitBreakerFactory.create("user-service");
	}

	@Override
	public Map<UserId, UserProfile> findByIds(Collection<UserId> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return Map.of();
		}
		String ids = userIds.stream()
			.map(userId -> userId.value().toString())
			.distinct()
			.collect(java.util.stream.Collectors.joining(","));
		return runWithUserServiceCircuitBreaker(() -> {
			UserProfileResponse[] response = restClient.get()
				.uri(uri -> uri.path("/internal/users/profiles").queryParam("ids", ids).build())
				.header("Authorization", "Bearer " + jwtService.createServiceToken("user-service"))
				.retrieve()
				.body(UserProfileResponse[].class);
			if (response == null) {
				return Map.of();
			}
			return java.util.Arrays.stream(response)
				.collect(java.util.stream.Collectors.toMap(user -> new UserId(user.id()),
						user -> new UserProfile(new UserId(user.id()), user.name(), user.surname(), user.username()),
						(first, ignored) -> first, LinkedHashMap::new));
		}, "User profiles could not be resolved");
	}

	private <T> T runWithUserServiceCircuitBreaker(Supplier<T> remoteCall, String failureMessage) {
		return userServiceCircuitBreaker.run(remoteCall, exception -> {
			if (exception instanceof IllegalStateException illegalStateException
					&& failureMessage.equals(illegalStateException.getMessage())) {
				throw illegalStateException;
			}
			if (exception instanceof RestClientException) {
				throw new IllegalStateException(failureMessage, exception);
			}
			throw new IllegalStateException(failureMessage, exception);
		});
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record UserProfileResponse(Long id, String name, String surname, String username) {
	}
}
