package com.rione.notification.infrastructure.persistence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

import com.rione.notification.application.port.out.NotificationRepository;
import com.rione.notification.domain.Notification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

class LocalNotificationDataResetInitializerTest {

	@Test
	void resetsAndReseedsNotificationsOnEveryStartup() {
		EntityManager entityManager = mock(EntityManager.class);
		Query truncateQuery = mock(Query.class);
		when(entityManager.createNativeQuery("TRUNCATE TABLE notifications RESTART IDENTITY")).thenReturn(truncateQuery);
		NotificationRepository notifications = mock(NotificationRepository.class);

		new LocalNotificationDataResetInitializer(entityManager, notifications).run(mock(ApplicationArguments.class));

		verify(truncateQuery, times(1)).executeUpdate();
		verify(notifications, times(18)).save(any(Notification.class));
	}
}
