package com.rione.social.infrastructure.proxy;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rione.social.application.port.out.NeighborhoodMembership;
import com.rione.social.domain.model.UserId;

@Component
class UserServiceProxy implements NeighborhoodMembership {

	private final RestClient restClient;

	UserServiceProxy(RestClient.Builder restClientBuilder,
			@Value("${rione.clients.user-service.base-url:http://localhost:8081}") String userServiceBaseUrl) {
		this.restClient = restClientBuilder.baseUrl(userServiceBaseUrl).build();
	}

	@Override
	public boolean sameNeighborhood(UserId firstUser, UserId secondUser) {
		Optional<Long> firstNeighborhood = neighborhoodOf(firstUser);
		Optional<Long> secondNeighborhood = neighborhoodOf(secondUser);
		return firstNeighborhood.isPresent() && firstNeighborhood.equals(secondNeighborhood);
	}

	private Optional<Long> neighborhoodOf(UserId userId) {
		try {
			UserResponse response = restClient.get()
				.uri("/users/{userId}", userId.value())
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
	record UserResponse(Long neighborhoodId) {
	}
}
