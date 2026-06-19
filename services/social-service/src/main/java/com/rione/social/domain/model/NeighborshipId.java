package com.rione.social.domain.model;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record NeighborshipId(Long value) implements DomainId {

	public NeighborshipId {
		DomainId.validate(value, "Neighborship id");
	}
}
