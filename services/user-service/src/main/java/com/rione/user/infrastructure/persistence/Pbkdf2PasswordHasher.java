package com.rione.user.infrastructure.persistence;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

import com.rione.user.application.port.out.PasswordHasher;

@Component
class Pbkdf2PasswordHasher implements PasswordHasher {

	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 120_000;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_LENGTH = 16;
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	@Override
	public String hash(String rawPassword) {
		try {
			byte[] salt = new byte[SALT_LENGTH];
			SECURE_RANDOM.nextBytes(salt);
			return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$"
					+ HexFormat.of().formatHex(derive(rawPassword, salt, ITERATIONS));
		}
		catch (Exception exception) {
			throw new IllegalStateException("Password hashing failed", exception);
		}
	}

	@Override
	public boolean matches(String rawPassword, String passwordHash) {
		try {
			String[] parts = passwordHash.split("\\$");
			if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {
				return false;
			}
			int iterations = Integer.parseInt(parts[1]);
			byte[] salt = Base64.getDecoder().decode(parts[2]);
			String actualHash = HexFormat.of().formatHex(derive(rawPassword, salt, iterations));
			return actualHash.equals(parts[3]);
		}
		catch (Exception exception) {
			return false;
		}
	}

	private byte[] derive(String rawPassword, byte[] salt, int iterations) throws Exception {
		KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, iterations, KEY_LENGTH);
		return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
	}
}
