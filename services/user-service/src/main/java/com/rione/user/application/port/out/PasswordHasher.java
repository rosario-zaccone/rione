package com.rione.user.application.port.out;

import com.rione.common.application.OutPort;

@OutPort
public interface PasswordHasher {

	String hash(String rawPassword);

	boolean matches(String rawPassword, String passwordHash);
}
