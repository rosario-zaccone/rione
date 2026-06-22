package com.rione.user.application.service;

import org.springframework.stereotype.Service;

import com.rione.user.application.port.in.UserService;
import com.rione.user.application.port.out.PasswordHasher;
import com.rione.user.application.port.out.UserRepository;
import com.rione.user.domain.model.Biography;
import com.rione.user.domain.model.BirthDate;
import com.rione.user.domain.model.FullName;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.NeighborhoodId;
import com.rione.user.domain.model.Password;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;
import com.rione.user.domain.model.Username;

@Service
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
		User user = User.register(new FullName(command.name(), command.surname()), username, mail,
				new NeighborhoodId(command.neighborhoodId()), new BirthDate(command.birthDate()), new Biography(command.bio()),
				new Password(passwordHasher.hash(command.password())));
		return toResponse(userRepository.save(user));
	}

	@Override
	public UserResponse logIn(LogInCommand command) {
		User user = userRepository.findByMail(new Mail(command.mail()))
			.orElseThrow(() -> new UserApplicationException("Invalid mail or password"));
		if (!passwordHasher.matches(command.password(), user.password().hash())) {
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
				new NeighborhoodId(command.neighborhoodId()), new BirthDate(command.birthDate()),
				new Biography(command.bio()));
		return toResponse(userRepository.save(user));
	}

	@Override
	public UserResponse getUser(Long userId) {
		return toResponse(findExisting(new UserId(userId)));
	}

	@Override
	public UserNeighborhoodResponse getUserNeighborhood(Long userId) {
		User user = findExisting(new UserId(userId));
		return new UserNeighborhoodResponse(user.id().value(), user.neighborhoodId().value());
	}

	private User findExisting(UserId userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UserApplicationException("User not found"));
	}

	private UserResponse toResponse(User user) {
		return new UserResponse(user.id().value(), user.fullName().name(), user.fullName().surname(),
				user.username().value(), user.mail().mail(), user.neighborhoodId().value(), user.birthDate().value(),
				user.bio().info(), user.isAdmin());
	}
}
