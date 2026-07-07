package com.rione.social.infrastructure.proxy;

import java.util.Optional;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rione.social.application.port.out.NeighborhoodMembership;
import com.rione.social.application.port.out.UserDirectory;
import com.rione.social.domain.model.UserId;
import com.rione.social.infrastructure.config.JwtService;

@Component
class UserServiceProxy implements NeighborhoodMembership, UserDirectory {

	private final RestClient restClient;
	private final JwtService jwtService;
	private final CircuitBreaker userServiceCircuitBreaker;

	UserServiceProxy(RestClient.Builder restClientBuilder,
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
		String ids = userIds.stream().map(userId -> userId.value().toString()).distinct().collect(java.util.stream.Collectors.joining(","));
		return runWithUserServiceCircuitBreaker(() -> {
			UserSearchResponse[] response = restClient.get()
				.uri(uri -> uri.path("/internal/users/profiles").queryParam("ids", ids).build())
				.header("Authorization", "Bearer " + jwtService.createServiceToken())
				.retrieve()
				.body(UserSearchResponse[].class);
			if (response == null) {
				return Map.of();
			}
			return java.util.Arrays.stream(response)
				.collect(java.util.stream.Collectors.toMap(user -> new UserId(user.id()),
						user -> new UserProfile(new UserId(user.id()), user.name(), user.surname(), user.username()),
						(first, ignored) -> first, LinkedHashMap::new));
		}, "User profiles could not be resolved");
	}

	@Override
	public boolean sameNeighborhood(UserId firstUser, UserId secondUser) {
		Optional<Long> firstNeighborhood = neighborhoodOf(firstUser);
		Optional<Long> secondNeighborhood = neighborhoodOf(secondUser);
		return firstNeighborhood.isPresent() && firstNeighborhood.equals(secondNeighborhood);
	}

	@Override
	public List<UserProfile> searchInNeighborhood(UserId requester, String query) {
		Optional<Long> neighborhood = neighborhoodOf(requester);
		if (neighborhood.isEmpty()) {
			return List.of();
		}
		return runWithUserServiceCircuitBreaker(() -> {
			UserSearchResponse[] response = restClient.get()
				.uri(uri -> uri.path("/internal/users/search")
					.queryParam("neighborhoodId", neighborhood.get())
					.queryParam("query", query)
					.build())
				.header("Authorization", "Bearer " + jwtService.createServiceToken())
				.retrieve()
				.body(UserSearchResponse[].class);
			if (response == null) {
				return List.of();
			}
			return java.util.Arrays.stream(response)
				.map(user -> new UserProfile(new UserId(user.id()), user.name(), user.surname(), user.username()))
				.toList();
		}, "Users could not be searched");
	}

	private Optional<Long> neighborhoodOf(UserId userId) {
		return runWithUserServiceCircuitBreaker(() -> {
			try {
				UserResponse response = restClient.get()
					.uri("/internal/users/{userId}/neighborhood", userId.value())
					.header("Authorization", "Bearer " + jwtService.createServiceToken())
					.retrieve()
					.body(UserResponse.class);
				return response == null ? Optional.empty() : Optional.ofNullable(response.neighborhoodId());
			}
			catch (RestClientResponseException exception) {
				if (exception.getStatusCode().value() == 404) {
					return Optional.empty();
				}
				throw new IllegalStateException("User neighborhood could not be resolved", exception);
			}
			catch (RestClientException exception) {
				throw new IllegalStateException("User neighborhood could not be resolved", exception);
			}
		}, "User neighborhood could not be resolved");
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
	record UserResponse(Long userId, Long neighborhoodId) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record UserSearchResponse(Long id, String name, String surname, String username) {
	}
}
