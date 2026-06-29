package com.rione.post.domain.model;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record NeighborhoodId(Long value) implements DomainId {

	public NeighborhoodId {
		DomainId.validate(value, "Neighborhood id");
	}
}
