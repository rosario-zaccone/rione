package com.rione.user.domain.model;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record CityId(Long value) implements DomainId {

	public CityId {
		DomainId.validate(value, "City id");
	}
}
