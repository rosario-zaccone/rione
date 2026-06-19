package com.rione.user.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "neighborhoods")
public class NeighborhoodJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "city_id", nullable = false)
	private CityJpaEntity city;

	protected NeighborhoodJpaEntity() {
	}

	public NeighborhoodJpaEntity(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public void setCity(CityJpaEntity city) {
		this.city = city;
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name;
	}
}
