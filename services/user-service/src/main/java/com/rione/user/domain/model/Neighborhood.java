package com.rione.user.domain.model;

@DDDAggregateRoot
public class Neighborhood {

	private final NeighborhoodId id;
	private final String name;
	private final Location location;

	public Neighborhood(NeighborhoodId id, String name, Location location) {
		if (id == null) {
			throw new DomainException("Neighborhood id is required");
		}
		if (name == null || name.isBlank()) {
			throw new DomainException("Neighborhood name is required");
		}
		if (location == null) {
			throw new DomainException("Neighborhood location is required");
		}
		this.id = id;
		this.name = name.trim();
		this.location = location;
	}

	public NeighborhoodId id() {
		return id;
	}

	public String name() {
		return name;
	}

	public Location location() {
		return location;
	}
}
