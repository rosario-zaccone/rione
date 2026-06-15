package com.rione.user.application.port.in;

import java.time.LocalDateTime;
public interface UserService {

	UserResponse signUp(SignUpCommand command);

	UserResponse logIn(LogInCommand command);

	UserResponse updateProfile(UpdateProfileCommand command);

	UserResponse getUser(Long userId);

	record SignUpCommand(String name, String surname, String username, String mail, String password,
			Long neighborhoodId, String neighborhoodName, String city, String country, LocalDateTime birthDate,
			String bio) {
	}

	record LogInCommand(String mail, String password) {
	}

	record UpdateProfileCommand(Long userId, String name, String surname, String username, Long neighborhoodId,
			String neighborhoodName, String city, String country, LocalDateTime birthDate, String bio) {
	}

	record UserResponse(Long id, String name, String surname, String username, String mail, Long neighborhoodId,
			String neighborhoodName, String city, String country, LocalDateTime birthDate, String bio,
			boolean admin) {
	}
}
