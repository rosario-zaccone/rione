package com.rione.user.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record NeighborhoodId(Long value) {

	public NeighborhoodId {
		if (value == null || value <= 0) {
			throw new DomainException("Neighborhood id must be positive");
		}
	}
}
