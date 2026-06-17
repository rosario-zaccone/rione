package com.rione.social.infrastructure.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.NeighborRequestId;
import com.rione.social.domain.model.RequestStatus;
import com.rione.social.domain.model.UserId;

@Repository
class InMemoryNeighborRequestRepository implements NeighborRequestRepository {

	private final AtomicLong sequence = new AtomicLong();
	private final Map<NeighborRequestId, NeighborRequest> requests = new ConcurrentHashMap<>();

	@Override
	public NeighborRequestId nextIdentity() {
		return new NeighborRequestId(sequence.incrementAndGet());
	}

	@Override
	public NeighborRequest save(NeighborRequest request) {
		requests.put(request.id(), request);
		return request;
	}

	@Override
	public Optional<NeighborRequest> findById(NeighborRequestId id) {
		return Optional.ofNullable(requests.get(id));
	}

	@Override
	public boolean existsPendingBetween(UserId sender, UserId receiver) {
		return requests.values()
			.stream()
			.anyMatch(request -> request.status() == RequestStatus.PENDING
					&& samePair(request.sender(), request.receiver(), sender, receiver));
	}

	private boolean samePair(UserId existingSender, UserId existingReceiver, UserId sender, UserId receiver) {
		return existingSender.equals(sender) && existingReceiver.equals(receiver)
				|| existingSender.equals(receiver) && existingReceiver.equals(sender);
	}
}
