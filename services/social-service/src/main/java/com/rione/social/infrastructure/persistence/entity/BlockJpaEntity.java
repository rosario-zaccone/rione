package com.rione.social.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "blocks")
public class BlockJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long blockerId;

	@Column(nullable = false)
	private Long blockedId;

	protected BlockJpaEntity() {
	}

	public BlockJpaEntity(Long id, Long blockerId, Long blockedId) {
		this.id = id;
		this.blockerId = blockerId;
		this.blockedId = blockedId;
	}

	public Long id() {
		return id;
	}

	public Long blockerId() {
		return blockerId;
	}

	public Long blockedId() {
		return blockedId;
	}
}
