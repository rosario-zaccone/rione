package com.rione.notification.infrastructure.web;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rione.notification.application.port.in.NotificationService;

class NotificationControllerIntegrationTest {

	private MockMvc mockMvc;
	private NotificationService notificationService;
	private CurrentUser currentUser;

	@BeforeEach
	void setUp() {
		notificationService = org.mockito.Mockito.mock(NotificationService.class);
		currentUser = org.mockito.Mockito.mock(CurrentUser.class);
		when(currentUser.id()).thenReturn(2L);
		mockMvc = MockMvcBuilders
			.standaloneSetup(new NotificationController(notificationService, currentUser), new NotificationExceptionHandler())
			.build();
	}

	@Test
	void listsUserNotificationsThroughHttpContract() throws Exception {
		when(notificationService.getNotifications(2L))
			.thenReturn(List.of(new NotificationService.NotificationResponse(1L, 2L, 1L, 10L, null, "REQUEST_RECEIVED",
					"Neighbor request received", "User 1 sent you a neighbor request",
					LocalDateTime.of(2026, 1, 1, 10, 0), null)));

		mockMvc.perform(get("/notifications/me"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].recipientId").value(2L))
			.andExpect(jsonPath("$[0].type").value("REQUEST_RECEIVED"));

		verify(notificationService).getNotifications(2L);
	}

	@Test
	void marksNotificationReadThroughHttpContract() throws Exception {
		when(notificationService.markNotificationRead(new NotificationService.MarkNotificationReadCommand(2L, 1L)))
			.thenReturn(new NotificationService.NotificationResponse(1L, 2L, 1L, 10L, null, "REQUEST_RECEIVED",
					"Neighbor request received", "User 1 sent you a neighbor request",
					LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 1)));

		mockMvc.perform(patch("/notifications/me/1/read"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.readAt").exists());
	}
}
