package com.rione.user.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.rione.user.PostgresIntegrationSupport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIT extends PostgresIntegrationSupport {

	@LocalServerPort
	private int port;

	@Test
	void signsUpUserThroughRestApi() throws Exception {
		HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/users"))
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString("""
				{
				  "name": "Ada",
				  "surname": "Lovelace",
				  "username": "ada.web",
				  "mail": "ada.web@rione.test",
				  "password": "password123",
				  "neighborhoodId": 1,
				  "neighborhoodName": "Centro",
				  "city": "Bologna",
				  "country": "Italy",
				  "birthDate": "1990-01-01T00:00:00",
				  "bio": "hello"
				}
				"""))
			.build();

		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(201, response.statusCode());
		assertTrue(response.body().contains("\"username\":\"ada.web\""));
		assertTrue(response.body().contains("\"neighborhoodName\":\"Centro\""));
	}
}
