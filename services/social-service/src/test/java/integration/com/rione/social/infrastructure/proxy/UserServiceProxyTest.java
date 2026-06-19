package com.rione.social.infrastructure.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.rione.social.domain.model.UserId;

class UserServiceProxyTest {

	private MockRestServiceServer server;
	private UserServiceProxy proxy;

	@BeforeEach
	void setUp() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		proxy = new UserServiceProxy(builder, "http://user-service.test");
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
		server.expect(once(), requestTo("http://user-service.test/users/2")).andRespond(withStatus(HttpStatus.NOT_FOUND));

		assertThat(proxy.sameNeighborhood(new UserId(1L), new UserId(2L))).isFalse();

		server.verify();
	}

	@Test
	void failsWhenUserServiceCannotResolveNeighborhood() {
		userServiceReturns(1L, 10L);
		server.expect(once(), requestTo("http://user-service.test/users/2")).andRespond(withServerError());

		assertThatThrownBy(() -> proxy.sameNeighborhood(new UserId(1L), new UserId(2L)))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("User neighborhood could not be resolved");

		server.verify();
	}

	private void userServiceReturns(Long userId, Long neighborhoodId) {
		server.expect(once(), requestTo("http://user-service.test/users/" + userId))
			.andRespond(withSuccess("{\"id\":" + userId + ",\"neighborhoodId\":" + neighborhoodId + "}",
					MediaType.APPLICATION_JSON));
	}
}
