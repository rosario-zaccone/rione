package com.rione.user.domain.model;

@DDDValueObject
public record NeighborhoodId(Long value) {

	public NeighborhoodId {
		if (value == null || value <= 0) {
			throw new DomainException("Neighborhood id must be positive");
		}
	}
}
