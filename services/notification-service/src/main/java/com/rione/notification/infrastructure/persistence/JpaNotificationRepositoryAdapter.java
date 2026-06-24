package com.rione.notification.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.rione.notification.application.port.out.NotificationRepository;
import com.rione.notification.domain.Notification;
import com.rione.notification.domain.NotificationId;
import com.rione.notification.domain.UserId;

@Repository
class JpaNotificationRepositoryAdapter implements NotificationRepository {

	private final SpringDataNotificationJpaRepository repository;

	JpaNotificationRepositoryAdapter(SpringDataNotificationJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public Notification save(Notification notification) {
		return toDomain(repository.save(toEntity(notification)));
	}

	@Override
	public Optional<Notification> findById(NotificationId id) {
		return repository.findById(id.value()).map(this::toDomain);
	}

	@Override
	public List<Notification> findByRecipient(UserId recipient) {
		return repository.findByRecipientIdOrderByOccurredAtDesc(recipient.value()).stream().map(this::toDomain).toList();
	}

	private NotificationJpaEntity toEntity(Notification notification) {
		Long id = notification.id() == null ? null : notification.id().value();
		return new NotificationJpaEntity(id, notification.recipient().value(), notification.actor().value(),
				notification.requestId(), notification.type(), notification.title(), notification.message(),
				notification.occurredAt(), notification.readAt());
	}

	private Notification toDomain(NotificationJpaEntity entity) {
		return Notification.restore(new NotificationId(entity.id()), new UserId(entity.recipientId()),
				new UserId(entity.actorId()), entity.requestId(), entity.type(), entity.title(), entity.message(),
				entity.occurredAt(), entity.readAt());
	}
}
