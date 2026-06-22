package com.rione.social.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.rione.social.application.port.out.NeighborRequestRepository;
import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.NeighborRequestId;
import com.rione.social.domain.model.RequestStatus;
import com.rione.social.domain.model.UserId;
import com.rione.social.infrastructure.persistence.entity.NeighborRequestJpaEntity;

@Repository
class JpaNeighborRequestRepositoryAdapter implements NeighborRequestRepository {

	private final SpringDataNeighborRequestJpaRepository repository;

	JpaNeighborRequestRepositoryAdapter(SpringDataNeighborRequestJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public NeighborRequest save(NeighborRequest request) {
		return toDomain(repository.save(toEntity(request)));
	}

	@Override
	public Optional<NeighborRequest> findById(NeighborRequestId id) {
		return repository.findById(id.value()).map(this::toDomain);
	}

	@Override
	public List<NeighborRequest> findBySender(UserId sender) {
		return repository.findBySenderId(sender.value()).stream().map(this::toDomain).toList();
	}

	@Override
	public List<NeighborRequest> findByReceiver(UserId receiver) {
		return repository.findByReceiverId(receiver.value()).stream().map(this::toDomain).toList();
	}

	@Override
	public boolean existsPendingBetween(UserId sender, UserId receiver) {
		return repository.existsByStatusAndSenderIdAndReceiverId(RequestStatus.PENDING, sender.value(),
				receiver.value())
				|| repository.existsByStatusAndSenderIdAndReceiverId(RequestStatus.PENDING, receiver.value(),
						sender.value());
	}

	@Override
	public List<NeighborRequest> findPendingInvolving(UserId userId) {
		return repository
			.findByStatusAndSenderIdOrStatusAndReceiverId(RequestStatus.PENDING, userId.value(), RequestStatus.PENDING,
					userId.value())
			.stream()
			.map(this::toDomain)
			.toList();
	}

	@Override
	public void delete(NeighborRequestId id) {
		repository.deleteById(id.value());
	}

	private NeighborRequestJpaEntity toEntity(NeighborRequest request) {
		Long id = request.id() == null ? null : request.id().value();
		return new NeighborRequestJpaEntity(id, request.sender().value(), request.receiver().value(), request.date(),
				request.status());
	}

	private NeighborRequest toDomain(NeighborRequestJpaEntity entity) {
		return NeighborRequest.restore(new NeighborRequestId(entity.id()), new UserId(entity.senderId()),
				new UserId(entity.receiverId()), entity.date(), entity.status());
	}
}
