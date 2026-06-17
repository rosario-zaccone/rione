package com.rione.social.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record NeighborshipId(Long value) {

	public NeighborshipId {
		if (value == null || value <= 0) {
			throw new DomainException("Neighborship id must be positive");
		}
	}
}
