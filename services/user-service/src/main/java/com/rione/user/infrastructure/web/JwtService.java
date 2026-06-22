package com.rione.user.infrastructure.web;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class JwtService {

	private static final String HMAC_ALGORITHM = "HmacSHA256";

	private final ObjectMapper objectMapper;
	private final byte[] userSecret;
	private final byte[] serviceSecret;
	private final long expirationMs;

	public JwtService(ObjectMapper objectMapper, @Value("${rione.security.jwt.secret}") String secret,
			@Value("${rione.security.jwt.service-secret}") String serviceSecret,
			@Value("${rione.security.jwt.expiration-ms}") long expirationMs) {
		this.objectMapper = objectMapper;
		this.userSecret = secret.getBytes(StandardCharsets.UTF_8);
		this.serviceSecret = serviceSecret.getBytes(StandardCharsets.UTF_8);
		this.expirationMs = expirationMs;
	}

	public String createToken(Long userId, boolean admin) {
		try {
			Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
			Instant now = Instant.now();
			Map<String, Object> payload = new LinkedHashMap<>();
			payload.put("sub", userId.toString());
			payload.put("admin", admin);
			payload.put("service", false);
			payload.put("iat", now.getEpochSecond());
			payload.put("exp", now.plusMillis(expirationMs).getEpochSecond());
			String unsigned = encodeJson(header) + "." + encodeJson(payload);
			return unsigned + "." + sign(unsigned, userSecret);
		}
		catch (Exception exception) {
			throw new IllegalStateException("Unable to create JWT", exception);
		}
	}

	public AuthenticatedPrincipal parse(String token) {
		try {
			String[] parts = token.split("\\.");
			if (parts.length != 3) {
				throw new IllegalArgumentException("Invalid JWT");
			}
			Map<String, Object> payload = objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]),
					new TypeReference<>() {
					});
			boolean service = Boolean.TRUE.equals(payload.get("service"));
			String unsigned = parts[0] + "." + parts[1];
			byte[] signingSecret = service ? serviceSecret : userSecret;
			if (!constantTimeEquals(sign(unsigned, signingSecret), parts[2])) {
				throw new IllegalArgumentException("Invalid JWT signature");
			}
			long expiresAt = ((Number) payload.get("exp")).longValue();
			if (Instant.now().getEpochSecond() >= expiresAt) {
				throw new IllegalArgumentException("Expired JWT");
			}
			String subject = payload.get("sub").toString();
			boolean admin = Boolean.TRUE.equals(payload.get("admin"));
			if (service) {
				if (admin || !"social-service".equals(subject) || !"user-service".equals(payload.get("aud"))) {
					throw new IllegalArgumentException("Invalid service JWT claims");
				}
			}
			else {
				Long.valueOf(subject);
			}
			return new AuthenticatedPrincipal(subject, admin, service);
		}
		catch (Exception exception) {
			throw new AuthorizationException("Authentication is invalid", true);
		}
	}

	private String encodeJson(Map<String, Object> value) throws Exception {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(objectMapper.writeValueAsBytes(value));
	}

	private String sign(String value, byte[] signingSecret) throws Exception {
		Mac mac = Mac.getInstance(HMAC_ALGORITHM);
		mac.init(new SecretKeySpec(signingSecret, HMAC_ALGORITHM));
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
