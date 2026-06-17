package com.rione.social.infrastructure.persistence;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.rione.social.application.port.out.NeighborshipRepository;
import com.rione.social.domain.model.Neighborship;
import com.rione.social.domain.model.NeighborshipId;
import com.rione.social.domain.model.UserId;

@Repository
class InMemoryNeighborshipRepository implements NeighborshipRepository {

	private final AtomicLong sequence = new AtomicLong();
	private final Map<NeighborshipId, Neighborship> neighborships = new ConcurrentHashMap<>();

	@Override
	public NeighborshipId nextIdentity() {
		return new NeighborshipId(sequence.incrementAndGet());
	}

	@Override
	public Neighborship save(Neighborship neighborship) {
		neighborships.put(neighborship.id(), neighborship);
		return neighborship;
	}

	@Override
	public List<Neighborship> findByFollower(UserId follower) {
		return neighborships.values()
			.stream()
			.filter(neighborship -> neighborship.follower().equals(follower))
			.toList();
	}

	@Override
	public boolean exists(UserId follower, UserId followed) {
		return neighborships.values()
			.stream()
			.anyMatch(neighborship -> neighborship.follower().equals(follower)
					&& neighborship.followed().equals(followed));
	}

	@Override
	public void deleteBetween(UserId firstUser, UserId secondUser) {
		neighborships.values()
			.removeIf(neighborship -> neighborship.follower().equals(firstUser)
					&& neighborship.followed().equals(secondUser)
					|| neighborship.follower().equals(secondUser) && neighborship.followed().equals(firstUser));
	}
}
