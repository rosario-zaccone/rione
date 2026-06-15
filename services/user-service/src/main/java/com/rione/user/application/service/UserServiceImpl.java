package com.rione.user.application.service;

import com.rione.user.application.port.in.UserService;
import com.rione.user.application.port.out.PasswordHasher;
import com.rione.user.application.port.out.UserRepository;
import com.rione.user.domain.model.Biography;
import com.rione.user.domain.model.FullName;
import com.rione.user.domain.model.Location;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.Neighborhood;
import com.rione.user.domain.model.NeighborhoodId;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;
import com.rione.user.domain.model.Username;

public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordHasher passwordHasher;

	public UserServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
	}

	@Override
	public UserResponse signUp(SignUpCommand command) {
		Mail mail = new Mail(command.mail());
		Username username = new Username(command.username());
		if (userRepository.existsByMail(mail)) {
			throw new UserApplicationException("Mail is already registered");
		}
		if (userRepository.existsByUsername(username)) {
			throw new UserApplicationException("Username is already registered");
		}
		User user = User.register(userRepository.nextIdentity(), new FullName(command.name(), command.surname()),
				username, mail,
				new Neighborhood(new NeighborhoodId(command.neighborhoodId()), command.neighborhoodName(),
						new Location(command.city(), command.country())),
				command.birthDate(), new Biography(command.bio()), passwordHasher.hash(command.password()));
		return toResponse(userRepository.save(user));
	}

	@Override
	public UserResponse logIn(LogInCommand command) {
		User user = userRepository.findByMail(new Mail(command.mail()))
			.orElseThrow(() -> new UserApplicationException("Invalid mail or password"));
		if (!passwordHasher.matches(command.password(), user.passwordHash())) {
			throw new UserApplicationException("Invalid mail or password");
		}
		return toResponse(user);
	}

	@Override
	public UserResponse updateProfile(UpdateProfileCommand command) {
		User user = findExisting(new UserId(command.userId()));
		Username username = new Username(command.username());
		if (!user.username().equals(username) && userRepository.existsByUsername(username)) {
			throw new UserApplicationException("Username is already registered");
		}
		user.updateProfile(new FullName(command.name(), command.surname()), username,
				new Neighborhood(new NeighborhoodId(command.neighborhoodId()), command.neighborhoodName(),
						new Location(command.city(), command.country())),
				command.birthDate(), new Biography(command.bio()));
		return toResponse(userRepository.save(user));
	}

	@Override
	public UserResponse getUser(Long userId) {
		return toResponse(findExisting(new UserId(userId)));
	}

	private User findExisting(UserId userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UserApplicationException("User not found"));
	}

	private UserResponse toResponse(User user) {
		return new UserResponse(user.id().value(), user.fullName().name(), user.fullName().surname(),
				user.username().value(), user.mail().mail(), user.neighborhood().id().value(), user.neighborhood().name(),
				user.neighborhood().location().city(), user.neighborhood().location().country(), user.birthDate(),
				user.bio().info(), user.isAdmin());
	}
}
