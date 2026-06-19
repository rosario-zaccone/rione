package com.rione.social.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.domain.model.Neighborship;
import com.rione.social.domain.model.NeighborshipId;
import com.rione.social.domain.model.UserId;
import com.rione.social.infrastructure.persistence.entity.NeighborshipJpaEntity;

@Repository
class JpaNeighborshipRepositoryAdapter implements NeighborshipRepository {

	private final SpringDataNeighborshipJpaRepository repository;

	JpaNeighborshipRepositoryAdapter(SpringDataNeighborshipJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public Neighborship save(Neighborship neighborship) {
		return toDomain(repository.save(toEntity(neighborship)));
	}

	@Override
	public List<Neighborship> findByFollower(UserId follower) {
		return repository.findByFollowerId(follower.value()).stream().map(this::toDomain).toList();
	}

	@Override
	public List<Neighborship> findByParticipant(UserId userId) {
		return repository.findByFollowerIdOrFollowedId(userId.value(), userId.value())
			.stream()
			.map(this::toDomain)
			.toList();
	}

	@Override
	public boolean exists(UserId follower, UserId followed) {
		return repository.existsByFollowerIdAndFollowedId(follower.value(), followed.value());
	}

	@Override
	@Transactional
	public void deleteBetween(UserId firstUser, UserId secondUser) {
		repository.deleteByFollowerIdAndFollowedId(firstUser.value(), secondUser.value());
		repository.deleteByFollowerIdAndFollowedId(secondUser.value(), firstUser.value());
	}

	private NeighborshipJpaEntity toEntity(Neighborship neighborship) {
		Long id = neighborship.id() == null ? null : neighborship.id().value();
		return new NeighborshipJpaEntity(id, neighborship.follower().value(), neighborship.followed().value(),
				neighborship.date());
	}

	private Neighborship toDomain(NeighborshipJpaEntity entity) {
		return Neighborship.restore(new NeighborshipId(entity.id()), new UserId(entity.followerId()),
				new UserId(entity.followedId()), entity.date());
	}
}
