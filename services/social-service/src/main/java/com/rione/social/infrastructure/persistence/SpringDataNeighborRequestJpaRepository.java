package com.rione.social.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rione.social.domain.model.RequestStatus;
import com.rione.social.infrastructure.persistence.entity.NeighborRequestJpaEntity;

interface SpringDataNeighborRequestJpaRepository extends JpaRepository<NeighborRequestJpaEntity, Long> {

	boolean existsByStatusAndSenderIdAndReceiverId(RequestStatus status, Long senderId, Long receiverId);

	List<NeighborRequestJpaEntity> findByStatusAndSenderIdOrStatusAndReceiverId(RequestStatus senderStatus,
			Long senderId, RequestStatus receiverStatus, Long receiverId);
}
