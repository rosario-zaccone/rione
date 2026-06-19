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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayRoutesConfigurationTest {

	private static final HttpServer userService = startBackend();

	@LocalServerPort
	private int gatewayPort;

	private final RestClient restClient = RestClient.create();

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("rione.gateway.routes.user-service-uri",
				() -> "http://localhost:" + userService.getAddress().getPort());
	}

	@AfterAll
	static void stopBackend() {
		userService.stop(0);
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
			.uri("http://localhost:" + gatewayPort + "/users/42")
			.retrieve()
			.toEntity(String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("\"path\":\"/users/42\"");
	}

	private static HttpServer startBackend() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
			server.createContext("/v3/api-docs", exchange -> writeJson(exchange, "{\"service\":\"user\"}"));
			server.createContext("/users/42", exchange -> writeJson(exchange, "{\"path\":\"/users/42\"}"));
			server.start();
			return server;
		} catch (IOException exception) {
			throw new IllegalStateException("Cannot start test backend", exception);
		}
	}

	private static void writeJson(com.sun.net.httpserver.HttpExchange exchange, String body) throws IOException {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, bytes.length);
		try (OutputStream outputStream = exchange.getResponseBody()) {
			outputStream.write(bytes);
		}
	}
}
