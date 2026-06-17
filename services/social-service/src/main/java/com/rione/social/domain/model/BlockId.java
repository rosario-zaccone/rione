package com.rione.social.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record BlockId(Long value) {

	public BlockId {
		if (value == null || value <= 0) {
			throw new DomainException("Block id must be positive");
		}
	}
}
