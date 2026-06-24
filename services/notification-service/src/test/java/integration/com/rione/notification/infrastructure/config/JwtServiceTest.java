package com.rione.notification.infrastructure.config;

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

import com.rione.notification.infrastructure.web.AuthorizationException;

import tools.jackson.databind.ObjectMapper;

class JwtServiceTest {

	private static final String SECRET = "change-this-demo-secret-key-1234567890";

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final JwtService jwtService = new JwtService(objectMapper, SECRET);

	@Test
	void parsesUserToken() {
		var principal = jwtService.parse(token(2L, false, Instant.now().plusSeconds(60)));

		assertThat(principal.userId()).isEqualTo(2L);
		assertThat(principal.admin()).isFalse();
	}

	@Test
	void rejectsInvalidSignature() {
		String token = token(2L, false, Instant.now().plusSeconds(60)) + "tampered";

		assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(AuthorizationException.class);
	}

	private String token(Long userId, boolean admin, Instant expiresAt) {
		try {
			Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
			Map<String, Object> payload = new LinkedHashMap<>();
			payload.put("sub", userId.toString());
			payload.put("admin", admin);
			payload.put("iat", Instant.now().getEpochSecond());
			payload.put("exp", expiresAt.getEpochSecond());
			String unsigned = encode(header) + "." + encode(payload);
			return unsigned + "." + sign(unsigned);
		}
		catch (Exception exception) {
			throw new IllegalStateException(exception);
		}
	}

	private String encode(Map<String, Object> value) throws Exception {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsBytes(value));
	}

	private String sign(String value) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
		return Base64.getUrlEncoder().withoutPadding()
			.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
	}
}
