package com.rione.notification.application.port.out;

import java.util.List;
import java.util.Optional;

import com.rione.common.application.OutPort;
import com.rione.notification.domain.Notification;
import com.rione.notification.domain.NotificationId;
import com.rione.notification.domain.UserId;

@OutPort
public interface NotificationRepository {

	Notification save(Notification notification);

	Optional<Notification> findById(NotificationId id);

	List<Notification> findByRecipient(UserId recipient);
}
