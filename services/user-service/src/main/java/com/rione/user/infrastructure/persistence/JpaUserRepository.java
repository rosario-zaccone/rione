package com.rione.user.infrastructure.persistence;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rione.user.application.port.out.UserRepository;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;
import com.rione.user.domain.model.Username;

import jakarta.persistence.EntityManager;

@Repository
class JpaUserRepository implements UserRepository {

	private final SpringDataUserJpaRepository repository;
	private final EntityManager entityManager;
	private final UserPersistenceMapper mapper = new UserPersistenceMapper();

	JpaUserRepository(SpringDataUserJpaRepository repository, EntityManager entityManager) {
		this.repository = repository;
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public UserId nextIdentity() {
		Object value = entityManager.createNativeQuery("select nextval('user_id_sequence')").getSingleResult();
		if (value instanceof BigInteger bigInteger) {
			return new UserId(bigInteger.longValue());
		}
		return new UserId(((Number) value).longValue());
	}

	@Override
	@Transactional
	public User save(User user) {
		UserJpaEntity entity = mapper.toEntity(user);
		UserJpaEntity saved;
		if (repository.existsById(user.id().value())) {
			saved = entityManager.merge(entity);
		}
		else {
			entityManager.persist(entity);
			saved = entity;
		}
		entityManager.flush();
		return mapper.toDomain(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findById(UserId userId) {
		return repository.findById(userId.value()).map(mapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findByMail(Mail mail) {
		return repository.findByMail(mail.mail()).map(mapper::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByMail(Mail mail) {
		return repository.existsByMail(mail.mail());
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByUsername(Username username) {
		return repository.existsByUsername(username.value());
	}
}
