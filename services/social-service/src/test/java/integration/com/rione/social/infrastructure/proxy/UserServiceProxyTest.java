package com.rione.social.infrastructure.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

import com.rione.social.domain.model.UserId;
import com.rione.social.infrastructure.config.JwtService;

class UserServiceProxyTest {

	private MockRestServiceServer server;
	private UserServiceProxy proxy;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		JwtService jwtService = mock(JwtService.class);
		when(jwtService.createServiceToken()).thenReturn("service-token");
		proxy = new UserServiceProxy(builder, "http://user-service.test", jwtService);
	}

	@Test
	void returnsTrueWhenUsersHaveSameNeighborhoodFromUserService() {
		userServiceReturns(1L, 10L);
		userServiceReturns(2L, 10L);

		assertThat(proxy.sameNeighborhood(new UserId(1L), new UserId(2L))).isTrue();

		server.verify();
	}

	@Test
	void returnsFalseWhenUsersHaveDifferentNeighborhoodsFromUserService() {
		userServiceReturns(1L, 10L);
		userServiceReturns(2L, 20L);

		assertThat(proxy.sameNeighborhood(new UserId(1L), new UserId(2L))).isFalse();

		server.verify();
	}

	@Test
	void returnsFalseWhenAUserDoesNotExistInUserService() {
		userServiceReturns(1L, 10L);
		server.expect(once(), requestTo("http://user-service.test/internal/users/2/neighborhood"))
			.andExpect(header("Authorization", "Bearer service-token"))
			.andRespond(withStatus(HttpStatus.NOT_FOUND));

		assertThat(proxy.sameNeighborhood(new UserId(1L), new UserId(2L))).isFalse();

		server.verify();
	}

	@Test
	void failsWhenUserServiceCannotResolveNeighborhood() {
		userServiceReturns(1L, 10L);
		server.expect(once(), requestTo("http://user-service.test/internal/users/2/neighborhood"))
			.andExpect(header("Authorization", "Bearer service-token"))
			.andRespond(withServerError());

		assertThatThrownBy(() -> proxy.sameNeighborhood(new UserId(1L), new UserId(2L)))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("User neighborhood could not be resolved");

		server.verify();
	}

	@Test
	void searchesUsersInRequestersNeighborhood() {
		userServiceReturns(1L, 10L);
		String url = UriComponentsBuilder.fromUriString("http://user-service.test/internal/users/search")
			.queryParam("neighborhoodId", 10L)
			.queryParam("query", "Ada Lovelace")
			.build()
			.encode()
			.toUriString();
		server.expect(once(), requestTo(url))
			.andExpect(header("Authorization", "Bearer service-token"))
			.andRespond(withSuccess("[{\"id\":2,\"name\":\"Ada\",\"surname\":\"Lovelace\",\"username\":\"ada\"}]",
					MediaType.APPLICATION_JSON));

		assertThat(proxy.searchInNeighborhood(new UserId(1L), "Ada Lovelace"))
			.extracting(profile -> profile.id().value(), profile -> profile.username())
			.containsExactly(org.assertj.core.groups.Tuple.tuple(2L, "ada"));
		server.verify();
	}

	@Test
	void resolvesMinimalProfilesByIds() {
		String url = UriComponentsBuilder.fromUriString("http://user-service.test/internal/users/profiles")
			.queryParam("ids", "2,3")
			.build()
			.encode()
			.toUriString();
		server.expect(once(), requestTo(url))
			.andExpect(header("Authorization", "Bearer service-token"))
			.andRespond(withSuccess("""
					[
					  {"id":2,"name":"Ada","surname":"Lovelace","username":"ada"},
					  {"id":3,"name":"Grace","surname":"Hopper","username":"grace"}
					]
					""", MediaType.APPLICATION_JSON));

		assertThat(proxy.findByIds(List.of(new UserId(2L), new UserId(3L))).values())
			.extracting(profile -> profile.id().value(), profile -> profile.username())
			.containsExactly(org.assertj.core.groups.Tuple.tuple(2L, "ada"),
					org.assertj.core.groups.Tuple.tuple(3L, "grace"));
		server.verify();
	}

	private void userServiceReturns(Long userId, Long neighborhoodId) {
		server.expect(once(), requestTo("http://user-service.test/internal/users/" + userId + "/neighborhood"))
			.andExpect(header("Authorization", "Bearer service-token"))
			.andRespond(withSuccess("{\"id\":" + userId + ",\"neighborhoodId\":" + neighborhoodId + "}",
					MediaType.APPLICATION_JSON));
	}
}
