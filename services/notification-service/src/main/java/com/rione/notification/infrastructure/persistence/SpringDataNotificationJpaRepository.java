package com.rione.notification.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataNotificationJpaRepository extends JpaRepository<NotificationJpaEntity, Long> {

	List<NotificationJpaEntity> findByRecipientIdOrderByOccurredAtDesc(Long recipientId);
}
