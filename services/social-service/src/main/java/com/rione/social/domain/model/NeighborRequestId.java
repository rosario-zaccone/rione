package com.rione.social.domain.model;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record NeighborRequestId(Long value) implements DomainId {

	public NeighborRequestId {
		DomainId.validate(value, "Neighbor request id");
	}
}
