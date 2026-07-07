package com.rione.post.infrastructure.proxy;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rione.post.application.port.out.PostVisibilityChecker;
import com.rione.post.domain.model.UserId;
import com.rione.post.infrastructure.config.JwtService;

@Component
class SocialPostVisibilityProxy implements PostVisibilityChecker {

	private final RestClient restClient;
	private final JwtService jwtService;
	private final CircuitBreaker socialServiceCircuitBreaker;

	SocialPostVisibilityProxy(RestClient.Builder restClientBuilder,
			@Value("${rione.clients.social-service.base-url:http://localhost:8082}") String socialServiceBaseUrl,
			JwtService jwtService, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
		this.restClient = restClientBuilder.baseUrl(socialServiceBaseUrl).build();
		this.jwtService = jwtService;
		this.socialServiceCircuitBreaker = circuitBreakerFactory.create("social-service");
	}

	@Override
	public RelationshipVisibility visibilityBetween(UserId viewer, UserId author) {
		return runWithSocialServiceCircuitBreaker(() -> {
			RelationshipVisibilityResponse response = restClient.get()
				.uri(uri -> uri.path("/internal/social/post-visibility")
					.queryParam("viewerId", viewer.value())
					.queryParam("authorId", author.value())
					.build())
				.header("Authorization", "Bearer " + jwtService.createServiceToken())
				.retrieve()
				.body(RelationshipVisibilityResponse.class);
			if (response == null) {
				throw new IllegalStateException("Post visibility could not be resolved");
			}
			return new RelationshipVisibility(response.sameNeighborhood(), response.activeNeighborship(),
					response.blocked());
		}, "Post visibility could not be resolved");
	}

	private <T> T runWithSocialServiceCircuitBreaker(Supplier<T> remoteCall, String failureMessage) {
		return socialServiceCircuitBreaker.run(remoteCall, exception -> {
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
	record RelationshipVisibilityResponse(boolean sameNeighborhood, boolean activeNeighborship, boolean blocked) {
	}
}
