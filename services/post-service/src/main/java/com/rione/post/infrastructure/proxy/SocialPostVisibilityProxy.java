package com.rione.post.infrastructure.proxy;

import org.springframework.beans.factory.annotation.Value;
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

	SocialPostVisibilityProxy(RestClient.Builder restClientBuilder,
			@Value("${rione.clients.social-service.base-url:http://localhost:8082}") String socialServiceBaseUrl,
			JwtService jwtService) {
		this.restClient = restClientBuilder.baseUrl(socialServiceBaseUrl).build();
		this.jwtService = jwtService;
	}

	@Override
	public RelationshipVisibility visibilityBetween(UserId viewer, UserId author) {
		try {
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
		}
		catch (RestClientException exception) {
			throw new IllegalStateException("Post visibility could not be resolved", exception);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record RelationshipVisibilityResponse(boolean sameNeighborhood, boolean activeNeighborship, boolean blocked) {
	}
}
