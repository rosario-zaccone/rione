package com.rione.user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rione.user.application.port.in.UserService;
import com.rione.user.application.port.out.PasswordHasher;
import com.rione.user.application.port.out.UserRepository;
import com.rione.user.application.service.UserServiceImpl;

@Configuration
public class UserServiceConfig {

	@Bean
	UserService userService(UserRepository userRepository, PasswordHasher passwordHasher) {
		return new UserServiceImpl(userRepository, passwordHasher);
	}
}
