package com.rione.user.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import com.rione.common.application.InPort;

@InPort
public interface UserService {

	UserResponse signUp(SignUpCommand command);

	UserResponse logIn(LogInCommand command);

	UserResponse updateProfile(UpdateProfileCommand command);

	UserResponse getUser(Long userId);

	PublicUserProfileResponse getPublicUserProfile(Long userId);

	UserNeighborhoodResponse getUserNeighborhood(Long userId);

	List<UserDirectoryResponse> getUserProfiles(List<Long> userIds);

	List<UserDirectoryResponse> searchUsers(Long neighborhoodId, String query);

	record SignUpCommand(String name, String surname, String username, String mail, String password,
			Long neighborhoodId, LocalDateTime birthDate, String bio) {
	}

	record LogInCommand(String mail, String password) {
	}

	record UpdateProfileCommand(Long userId, String name, String surname, String username, Long neighborhoodId,
			LocalDateTime birthDate, String bio) {
	}

	record UserResponse(Long id, String name, String surname, String username, String mail, Long neighborhoodId,
			LocalDateTime birthDate, String bio, boolean admin) {
	}

	record UserNeighborhoodResponse(Long userId, Long neighborhoodId) {
	}

	record UserDirectoryResponse(Long id, String name, String surname, String username) {
	}

	record PublicUserProfileResponse(Long id, String name, String surname, String username, Long neighborhoodId,
			String bio) {
	}
}
