package com.rione.social.domain.model;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record BlockId(Long value) implements DomainId {

	public BlockId {
		DomainId.validate(value, "Block id");
	}
}
