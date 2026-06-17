package com.rione.social.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record NeighborRequestId(Long value) {

	public NeighborRequestId {
		if (value == null || value <= 0) {
			throw new DomainException("Neighbor request id must be positive");
		}
	}
}
