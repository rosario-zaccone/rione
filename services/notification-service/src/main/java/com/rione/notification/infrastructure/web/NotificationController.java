package com.rione.notification.infrastructure.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rione.notification.application.port.in.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
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
	@Operation(summary = "List my notifications",
			description = "Only authenticated users can access this endpoint. It returns notifications owned by the authenticated recipient, including request notifications with request identifiers and post comment/reaction notifications with post identifiers.")
	List<NotificationService.NotificationResponse> getNotifications() {
		return notificationService.getNotifications(currentUser.id());
	}

	@PatchMapping("/{notificationId}/read")
	@Operation(summary = "Mark my notification as read",
			description = "Only authenticated users can access this endpoint. Only the recipient of the notification can mark it as read; notifications owned by another user are hidden as not found. The operation updates notification read state.")
	NotificationService.NotificationResponse markNotificationRead(@PathVariable @Positive Long notificationId) {
		return notificationService
			.markNotificationRead(new NotificationService.MarkNotificationReadCommand(currentUser.id(), notificationId));
	}
}
