package com.rione.user.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

class JwtServiceTest {

	private static final String USER_SECRET = "test-user-signing-secret-1234567890";
	private static final String SERVICE_SECRET = "test-service-signing-secret-1234567890";
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final JwtService jwtService = new JwtService(objectMapper, USER_SECRET, SERVICE_SECRET, 60_000);

	@Test
	void parsesServiceTokenSignedForUserService() throws Exception {
		AuthenticatedPrincipal principal = jwtService.parse(serviceToken(SERVICE_SECRET, "social-service", "user-service"));

		assertThat(principal.subject()).isEqualTo("social-service");
		assertThat(principal.service()).isTrue();
		assertThat(principal.admin()).isFalse();
	}

	@Test
	void rejectsServiceClaimSignedWithUserSecret() throws Exception {
		String token = serviceToken(USER_SECRET, "social-service", "user-service");

		assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(AuthorizationException.class);
	}

	@Test
	void rejectsServiceTokenForAnotherAudience() throws Exception {
		String token = serviceToken(SERVICE_SECRET, "social-service", "another-service");

		assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(AuthorizationException.class);
	}

	@Test
	void rejectsUntrustedServiceIdentity() throws Exception {
		String token = serviceToken(SERVICE_SECRET, "another-service", "user-service");

		assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(AuthorizationException.class);
	}

	private String serviceToken(String signingSecret, String subject, String audience) throws Exception {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("sub", subject);
		payload.put("aud", audience);
		payload.put("admin", false);
		payload.put("service", true);
		payload.put("iat", Instant.now().getEpochSecond());
		payload.put("exp", Instant.now().plusSeconds(60).getEpochSecond());
		String unsigned = encode(Map.of("alg", "HS256", "typ", "JWT")) + "." + encode(payload);
		return unsigned + "." + sign(unsigned, signingSecret);
	}

	private String encode(Map<String, Object> value) throws Exception {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsBytes(value));
	}

	private String sign(String value, String signingSecret) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
		return Base64.getUrlEncoder().withoutPadding()
			.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
	}
}
