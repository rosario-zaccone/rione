package com.rione.notification.infrastructure.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rione.notification.application.port.in.NotificationService;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/notifications/users/{userId}")
class NotificationController {

	private final NotificationService notificationService;

	NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping
	List<NotificationService.NotificationResponse> getNotifications(@PathVariable @Positive Long userId) {
		return notificationService.getNotifications(userId);
	}

	@PatchMapping("/{notificationId}/read")
	NotificationService.NotificationResponse markNotificationRead(@PathVariable @Positive Long userId,
			@PathVariable @Positive Long notificationId) {
		return notificationService
			.markNotificationRead(new NotificationService.MarkNotificationReadCommand(userId, notificationId));
	}
}
