package com.rione.user.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rione.common.domain.DDDAggregateRoot;

@DDDAggregateRoot
public class City {

	private final CityId id;
	private final String name;
	private final List<Neighborhood> neighborhoods;

	public City(CityId id, String name, List<Neighborhood> neighborhoods) {
		if (name == null || name.isBlank()) {
			throw new DomainException("City name is required");
		}
		if (neighborhoods == null || neighborhoods.isEmpty()) {
			throw new DomainException("City must contain at least one neighborhood");
		}
		this.id = id;
		this.name = name.trim();
		this.neighborhoods = List.copyOf(neighborhoods);
	}

	public static City create(String name, List<String> neighborhoodNames) {
		if (neighborhoodNames == null || neighborhoodNames.isEmpty()) {
			throw new DomainException("City must contain at least one neighborhood");
		}
		return new City(null, name, neighborhoodNames.stream().map(Neighborhood::create).toList());
	}

	public static City restore(CityId id, String name, List<Neighborhood> neighborhoods) {
		if (id == null) {
			throw new DomainException("City id is required");
		}
		return new City(id, name, neighborhoods);
	}

	public CityId id() {
		return id;
	}

	public String name() {
		return name;
	}

	public List<Neighborhood> neighborhoods() {
		return Collections.unmodifiableList(neighborhoods);
	}

	public City withNeighborhood(Neighborhood neighborhood) {
		if (neighborhood == null) {
			throw new DomainException("Neighborhood is required");
		}
		List<Neighborhood> updatedNeighborhoods = new ArrayList<>(neighborhoods);
		updatedNeighborhoods.add(neighborhood);
		return new City(id, name, updatedNeighborhoods);
	}
}
