package com.rione.user.infrastructure.config;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

import com.rione.user.application.port.out.PasswordHasher;
import com.rione.user.application.service.UserApplicationException;

@Component
public class Pbkdf2PasswordHasher implements PasswordHasher {

	private static final int ITERATIONS = 120_000;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_LENGTH = 16;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private final SecureRandom secureRandom = new SecureRandom();

	@Override
	public String hash(String rawPassword) {
		if (rawPassword == null || rawPassword.length() < 8) {
			throw new UserApplicationException("Password must be at least 8 characters");
		}
		byte[] salt = new byte[SALT_LENGTH];
		secureRandom.nextBytes(salt);
		byte[] hash = pbkdf2(rawPassword, salt);
		return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":"
				+ Base64.getEncoder().encodeToString(hash);
	}

	@Override
	public boolean matches(String rawPassword, String passwordHash) {
		if (rawPassword == null || passwordHash == null) {
			return false;
		}
		String[] parts = passwordHash.split(":");
		if (parts.length != 3) {
			return false;
		}
		byte[] salt = Base64.getDecoder().decode(parts[1]);
		byte[] expected = Base64.getDecoder().decode(parts[2]);
		byte[] actual = pbkdf2(rawPassword, salt);
		return constantTimeEquals(expected, actual);
	}

	private byte[] pbkdf2(String rawPassword, byte[] salt) {
		try {
			KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
			return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			throw new UserApplicationException("Password hashing is unavailable");
		}
	}

	private static boolean constantTimeEquals(byte[] expected, byte[] actual) {
		if (expected.length != actual.length) {
			return false;
		}
		int result = 0;
		for (int index = 0; index < expected.length; index++) {
			result |= expected[index] ^ actual[index];
		}
		return result == 0;
	}
}
