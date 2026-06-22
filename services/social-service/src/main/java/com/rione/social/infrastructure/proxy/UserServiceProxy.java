package com.rione.social.infrastructure.proxy;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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

	UserServiceProxy(RestClient.Builder restClientBuilder,
			@Value("${rione.clients.user-service.base-url:http://localhost:8081}") String userServiceBaseUrl,
			JwtService jwtService) {
		this.restClient = restClientBuilder.baseUrl(userServiceBaseUrl).build();
		this.jwtService = jwtService;
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
		try {
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
		}
		catch (RestClientException exception) {
			throw new IllegalStateException("Users could not be searched", exception);
		}
	}

	private Optional<Long> neighborhoodOf(UserId userId) {
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
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record UserResponse(Long userId, Long neighborhoodId) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record UserSearchResponse(Long id, String name, String surname, String username) {
	}
}
