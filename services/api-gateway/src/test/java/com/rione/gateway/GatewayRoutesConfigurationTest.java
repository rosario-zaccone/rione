package com.rione.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayRoutesConfigurationTest {

	private static final HttpServer userService = startUserBackend();

	private static final HttpServer socialService = startSocialBackend();

	@LocalServerPort
	private int gatewayPort;

	private final RestClient restClient = RestClient.create();

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("rione.gateway.routes.user-service-uri",
				() -> "http://localhost:" + userService.getAddress().getPort());
		registry.add("rione.gateway.routes.social-service-uri",
				() -> "http://localhost:" + socialService.getAddress().getPort());
	}

	@AfterAll
	static void stopBackend() {
		userService.stop(0);
		socialService.stop(0);
	}

	@Test
	void routesUserOpenApiDocsWithoutForwardingUsersPrefix() {
		ResponseEntity<String> response = restClient.get()
			.uri("http://localhost:" + gatewayPort + "/users/v3/api-docs")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("\"service\":\"user\"");
	}

	@Test
	void routesUserApiRequestsWithUsersPrefix() {
		ResponseEntity<String> response = restClient.get()
			.uri("http://localhost:" + gatewayPort + "/users/me")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("\"path\":\"/users/me\"");
	}

	@Test
	void routesNeighborRequestsAtRootToSocialService() {
		ResponseEntity<String> response = restClient.post()
			.uri("http://localhost:" + gatewayPort + "/neighbor-requests")
			.contentType(MediaType.APPLICATION_JSON)
			.body("{\"receiverId\":4}")
			.exchange((request, backendResponse) -> new ResponseEntity<>(
					new String(backendResponse.getBody().readAllBytes(), StandardCharsets.UTF_8),
					backendResponse.getHeaders(), backendResponse.getStatusCode()));

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Users must belong to the same neighborhood");
	}

	@Test
	void routesCurrentUsersSentNeighborRequestsAtRootToSocialService() {
		ResponseEntity<String> response = restClient.get()
			.uri("http://localhost:" + gatewayPort + "/me/neighbor-requests/sent")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("\"path\":\"/me/neighbor-requests/sent\"");
	}

	@Test
	void routesCurrentUsersReceivedNeighborRequestsAtRootToSocialService() {
		ResponseEntity<String> response = restClient.get()
			.uri("http://localhost:" + gatewayPort + "/me/neighbor-requests/received")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("\"path\":\"/me/neighbor-requests/received\"");
	}

	@Test
	void routesCurrentUsersNeighborshipsAtRootToSocialService() {
		ResponseEntity<String> response = restClient.get()
			.uri("http://localhost:" + gatewayPort + "/me/neighborships")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("\"path\":\"/me/neighborships\"");
	}

	@Test
	void routesRestfulSocialRelationshipMutationsAtRoot() {
		ResponseEntity<String> putResponse = restClient.put()
			.uri("http://localhost:" + gatewayPort + "/me/blocks/3")
			.retrieve()
			.toEntity(String.class);
		ResponseEntity<String> deleteResponse = restClient.delete()
			.uri("http://localhost:" + gatewayPort + "/me/neighborships/3")
			.retrieve()
			.toEntity(String.class);

		assertThat(putResponse.getBody()).contains("\"path\":\"/me/blocks/3\"");
		assertThat(deleteResponse.getBody()).contains("\"path\":\"/me/neighborships/3\"");
	}

	private static HttpServer startUserBackend() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
			server.createContext("/v3/api-docs", exchange -> writeJson(exchange, "{\"service\":\"user\"}"));
			server.createContext("/users/me", exchange -> writeJson(exchange, "{\"path\":\"/users/me\"}"));
			server.start();
			return server;
		} catch (IOException exception) {
			throw new IllegalStateException("Cannot start test backend", exception);
		}
	}

	private static HttpServer startSocialBackend() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
			server.createContext("/neighbor-requests", exchange -> {
				writeJson(exchange, "{\"detail\":\"Users must belong to the same neighborhood\"}",
						HttpStatus.BAD_REQUEST.value());
			});
			server.createContext("/me", exchange -> writeJson(exchange,
					"{\"path\":\"" + exchange.getRequestURI().getPath() + "\"}"));
			server.start();
			return server;
		} catch (IOException exception) {
			throw new IllegalStateException("Cannot start test backend", exception);
		}
	}

	private static void writeJson(com.sun.net.httpserver.HttpExchange exchange, String body) throws IOException {
		writeJson(exchange, body, HttpStatus.OK.value());
	}

	private static void writeJson(com.sun.net.httpserver.HttpExchange exchange, String body, int status)
			throws IOException {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(status, bytes.length);
		try (OutputStream outputStream = exchange.getResponseBody()) {
			outputStream.write(bytes);
		}
	}
}
