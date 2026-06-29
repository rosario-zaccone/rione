package com.rione.user.infrastructure.persistence;

import java.util.Optional;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

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
import com.rione.user.infrastructure.persistence.entity.UserJpaEntity;

@Repository
class JpaUserRepositoryAdapter implements UserRepository {

	private final SpringDataUserJpaRepository repository;

	JpaUserRepositoryAdapter(SpringDataUserJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public User save(User user) {
		return toDomain(repository.save(toEntity(user)));
	}

	@Override
	public Optional<User> findById(UserId userId) {
		return repository.findById(userId.value()).map(this::toDomain);
	}

	@Override
	public Optional<User> findByMail(Mail mail) {
		return repository.findByMail(mail.mail()).map(this::toDomain);
	}

	@Override
	public boolean existsByMail(Mail mail) {
		return repository.existsByMail(mail.mail());
	}

	@Override
	public boolean existsByUsername(Username username) {
		return repository.existsByUsername(username.value());
	}

	@Override
	public boolean existsByNeighborhoodIdIn(Collection<NeighborhoodId> neighborhoodIds) {
		return repository.existsByNeighborhoodIdIn(neighborhoodIds.stream().map(NeighborhoodId::value).toList());
	}

	@Override
	public List<User> searchByNeighborhood(NeighborhoodId neighborhoodId, String query) {
		return repository.searchByNeighborhood(neighborhoodId.value(), query).stream().map(this::toDomain).toList();
	}

	@Override
	public long countRegisteredUsers() {
		return repository.count();
	}

	@Override
	public long countRegisteredUsersSince(LocalDate date) {
		return repository.countByRegisteredAtGreaterThanEqual(date.atStartOfDay());
	}

	private UserJpaEntity toEntity(User user) {
		Long id = user.id() == null ? null : user.id().value();
		return new UserJpaEntity(id, user.fullName().name(), user.fullName().surname(), user.username().value(),
				user.mail().mail(), user.neighborhoodId().value(), user.birthDate().value(), user.bio().info(),
				user.password().hash(), user.isAdmin(), null);
	}

	private User toDomain(UserJpaEntity entity) {
		return User.restore(new UserId(entity.id()), new FullName(entity.name(), entity.surname()),
				new Username(entity.username()), new Mail(entity.mail()), new NeighborhoodId(entity.neighborhoodId()),
				new BirthDate(entity.birthDate()), new Biography(entity.bio()), new Password(entity.passwordHash()),
				entity.admin());
	}
}
