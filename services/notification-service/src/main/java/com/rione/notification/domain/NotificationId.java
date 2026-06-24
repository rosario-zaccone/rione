package com.rione.notification.domain;

import com.rione.common.domain.DDDValueObject;
import com.rione.common.domain.DomainId;

@DDDValueObject
public record NotificationId(Long value) implements DomainId {

	public NotificationId {
		DomainId.validate(value, "Notification id");
	}
}
