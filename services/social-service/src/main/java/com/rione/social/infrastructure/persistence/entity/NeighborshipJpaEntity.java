package com.rione.social.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "neighborships")
public class NeighborshipJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long followerId;

	@Column(nullable = false)
	private Long followedId;

	@Column(nullable = false)
	private LocalDateTime date;

	protected NeighborshipJpaEntity() {
	}

	public NeighborshipJpaEntity(Long id, Long followerId, Long followedId, LocalDateTime date) {
		this.id = id;
		this.followerId = followerId;
		this.followedId = followedId;
		this.date = date;
	}

	public Long id() {
		return id;
	}

	public Long followerId() {
		return followerId;
	}

	public Long followedId() {
		return followedId;
	}

	public LocalDateTime date() {
		return date;
	}
}
