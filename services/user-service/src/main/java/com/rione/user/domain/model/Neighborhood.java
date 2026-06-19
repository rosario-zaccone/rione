package com.rione.user.domain.model;

import com.rione.common.domain.DDDEntity;

@DDDEntity
public class Neighborhood {

	private final NeighborhoodId id;
	private final String name;

	public Neighborhood(NeighborhoodId id, String name) {
		if (name == null || name.isBlank()) {
			throw new DomainException("Neighborhood name is required");
		}
		this.id = id;
		this.name = name.trim();
	}

	public static Neighborhood create(String name) {
		return new Neighborhood(null, name);
	}

	public static Neighborhood restore(NeighborhoodId id, String name) {
		if (id == null) {
			throw new DomainException("Neighborhood id is required");
		}
		return new Neighborhood(id, name);
	}

	public NeighborhoodId id() {
		return id;
	}

	public String name() {
		return name;
	}
}
