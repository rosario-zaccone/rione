package com.rione.user.application.service;

import java.util.Comparator;
import java.util.List;

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
	public UserView signUp(SignUpCommand command) {
		Mail mail = new Mail(command.mail());
		Username username = new Username(command.username());
		if (userRepository.existsByMail(mail)) {
			throw new UserApplicationException("Mail is already registered");
		}
		if (userRepository.existsByUsername(username)) {
			throw new UserApplicationException("Username is already registered");
		}
		User user = User.register(userRepository.nextIdentity(), fullName(command.name(), command.surname()), username,
				mail, neighborhood(command.neighborhoodId(), command.neighborhoodName(), command.city(), command.country()),
				command.birthDate(), new Biography(command.bio()), passwordHasher.hash(command.password()));
		return toView(userRepository.save(user));
	}

	@Override
	public UserView logIn(LogInCommand command) {
		User user = userRepository.findByMail(new Mail(command.mail()))
			.orElseThrow(() -> new UserApplicationException("Invalid mail or password"));
		if (!passwordHasher.matches(command.password(), user.passwordHash())) {
			throw new UserApplicationException("Invalid mail or password");
		}
		return toView(user);
	}

	@Override
	public UserView updateProfile(UpdateProfileCommand command) {
		User user = findExisting(new UserId(command.userId()));
		Username username = new Username(command.username());
		if (!user.username().equals(username) && userRepository.existsByUsername(username)) {
			throw new UserApplicationException("Username is already registered");
		}
		user.updateProfile(fullName(command.name(), command.surname()), username,
				neighborhood(command.neighborhoodId(), command.neighborhoodName(), command.city(), command.country()),
				command.birthDate(), new Biography(command.bio()));
		return toView(userRepository.save(user));
	}

	@Override
	public UserView getUser(Long userId) {
		return toView(findExisting(new UserId(userId)));
	}

	@Override
	public List<UserView> searchNeighbours(SearchNeighboursQuery query) {
		User requester = findExisting(new UserId(query.requesterId()));
		return userRepository.search(query.query(), query.neighborhoodId()).stream()
			.filter(user -> !user.id().equals(requester.id()))
			.sorted(Comparator.comparing(user -> user.fullName().displayName()))
			.map(this::toView)
			.toList();
	}

	private User findExisting(UserId userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UserApplicationException("User not found"));
	}

	private static FullName fullName(String name, String surname) {
		return new FullName(name, surname);
	}

	private static Neighborhood neighborhood(Long id, String name, String city, String country) {
		return new Neighborhood(new NeighborhoodId(id), name, new Location(city, country));
	}

	private UserView toView(User user) {
		return new UserView(user.id().value(), user.fullName().name(), user.fullName().surname(),
				user.username().value(), user.mail().mail(), user.neighborhood().id().value(), user.neighborhood().name(),
				user.neighborhood().location().city(), user.neighborhood().location().country(), user.birthDate(),
				user.bio().info(), user.isAdmin());
	}
}
