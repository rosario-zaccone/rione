package com.rione.social.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import com.rione.social.domain.model.RequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "neighbor_requests")
public class NeighborRequestJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long senderId;

	@Column(nullable = false)
	private Long receiverId;

	@Column(nullable = false)
	private LocalDateTime date;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RequestStatus status;

	protected NeighborRequestJpaEntity() {
	}

	public NeighborRequestJpaEntity(Long id, Long senderId, Long receiverId, LocalDateTime date,
			RequestStatus status) {
		this.id = id;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.date = date;
		this.status = status;
	}

	public Long id() {
		return id;
	}

	public Long senderId() {
		return senderId;
	}

	public Long receiverId() {
		return receiverId;
	}

	public LocalDateTime date() {
		return date;
	}

	public RequestStatus status() {
		return status;
	}
}
