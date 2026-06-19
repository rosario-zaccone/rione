package com.rione.user.infrastructure.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cities")
public class CityJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String name;

	@OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<NeighborhoodJpaEntity> neighborhoods = new ArrayList<>();

	protected CityJpaEntity() {
	}

	public CityJpaEntity(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public void addNeighborhood(NeighborhoodJpaEntity neighborhood) {
		neighborhood.setCity(this);
		neighborhoods.add(neighborhood);
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public List<NeighborhoodJpaEntity> neighborhoods() {
		return List.copyOf(neighborhoods);
	}
}
