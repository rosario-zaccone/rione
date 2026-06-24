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
@RequestMapping("/notifications/me")
class NotificationController {

	private final NotificationService notificationService;
	private final CurrentUser currentUser;

	NotificationController(NotificationService notificationService, CurrentUser currentUser) {
		this.notificationService = notificationService;
		this.currentUser = currentUser;
	}

	@GetMapping
	List<NotificationService.NotificationResponse> getNotifications() {
		return notificationService.getNotifications(currentUser.id());
	}

	@PatchMapping("/{notificationId}/read")
	NotificationService.NotificationResponse markNotificationRead(@PathVariable @Positive Long notificationId) {
		return notificationService
			.markNotificationRead(new NotificationService.MarkNotificationReadCommand(currentUser.id(), notificationId));
	}
}
