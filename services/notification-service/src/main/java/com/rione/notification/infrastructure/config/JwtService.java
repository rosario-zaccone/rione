package com.rione.notification.infrastructure.config;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.rione.notification.infrastructure.web.AuthenticatedPrincipal;
import com.rione.notification.infrastructure.web.AuthorizationException;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtService {

	private static final String HMAC_ALGORITHM = "HmacSHA256";

	private final ObjectMapper objectMapper;
	private final byte[] secret;

	public JwtService(ObjectMapper objectMapper, @Value("${rione.security.jwt.secret}") String secret) {
		this.objectMapper = objectMapper;
		this.secret = secret.getBytes(StandardCharsets.UTF_8);
	}

	public AuthenticatedPrincipal parse(String token) {
		try {
			String[] parts = token.split("\\.");
			if (parts.length != 3) {
				throw new IllegalArgumentException("Invalid JWT");
			}
			String unsigned = parts[0] + "." + parts[1];
			if (!constantTimeEquals(sign(unsigned), parts[2])) {
				throw new IllegalArgumentException("Invalid JWT signature");
			}
			Map<String, Object> payload = objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]),
					new TypeReference<>() {
					});
			long expiresAt = ((Number) payload.get("exp")).longValue();
			if (Instant.now().getEpochSecond() >= expiresAt) {
				throw new IllegalArgumentException("Expired JWT");
			}
			Long userId = Long.valueOf(payload.get("sub").toString());
			boolean admin = Boolean.TRUE.equals(payload.get("admin"));
			return new AuthenticatedPrincipal(userId, admin);
		}
		catch (Exception exception) {
			throw new AuthorizationException("Authentication is invalid", true);
		}
	}

	private String sign(String value) throws Exception {
		Mac mac = Mac.getInstance(HMAC_ALGORITHM);
		mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
		return Base64.getUrlEncoder().withoutPadding()
			.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
	}

	private boolean constantTimeEquals(String expected, String actual) {
		byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
		byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);
		if (expectedBytes.length != actualBytes.length) {
			return false;
		}
		int result = 0;
		for (int i = 0; i < expectedBytes.length; i++) {
			result |= expectedBytes[i] ^ actualBytes[i];
		}
		return result == 0;
	}
}
