package com.rione.social.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import java.util.Map;

import org.junit.jupiter.api.Test;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

class JwtServiceTest {

	@Test
	void createsShortLivedServiceTokenForUserService() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		JwtService jwtService = new JwtService(objectMapper, "user-secret", "service-secret", 86_400_000,
				300_000, "social-service");

		String token = jwtService.createServiceToken();
		String payloadPart = token.split("\\.")[1];
		Map<String, Object> payload = objectMapper.readValue(Base64.getUrlDecoder().decode(payloadPart),
				new TypeReference<>() {
				});

		assertThat(payload).containsEntry("sub", "social-service")
			.containsEntry("aud", "user-service")
			.containsEntry("service", true)
			.containsEntry("admin", false);
		long lifetimeSeconds = ((Number) payload.get("exp")).longValue() - ((Number) payload.get("iat")).longValue();
		assertThat(lifetimeSeconds).isEqualTo(300);
	}
}
