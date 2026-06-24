package com.rione.notification.infrastructure.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.rione.notification.application.port.in.NotificationService;
import com.rione.notification.infrastructure.config.JwtService;

class NotificationSecurityIntegrationTest {

	private AnnotationConfigWebApplicationContext context;
	private MockMvc mockMvc;
	private JwtService jwtService;
	private NotificationService notificationService;

	@BeforeEach
	void setUp() {
		jwtService = mock(JwtService.class);
		notificationService = mock(NotificationService.class);
		context = new AnnotationConfigWebApplicationContext();
		context.setServletContext(new org.springframework.mock.web.MockServletContext());
		context.register(WebConfiguration.class, SecurityConfig.class, JwtAuthenticationFilter.class,
				NotificationController.class, NotificationExceptionHandler.class, CurrentUser.class);
		context.register((registry, environment) -> {
			registry.registerBean("notificationService", NotificationService.class,
					spec -> spec.supplier(ignored -> notificationService));
			registry.registerBean("jwtService", JwtService.class, spec -> spec.supplier(ignored -> jwtService));
		});
		context.refresh();
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.addFilters(context.getBean(FilterChainProxy.class))
			.build();
	}

	@AfterEach
	void tearDown() {
		context.close();
	}

	@Test
	void requiresAuthenticationForNotifications() throws Exception {
		mockMvc.perform(get("/notifications/me")).andExpect(status().isUnauthorized());
	}

	@Test
	void authenticatedUserCanListOwnNotifications() throws Exception {
		when(jwtService.parse("user-token")).thenReturn(new AuthenticatedPrincipal(2L, false));
		when(notificationService.getNotifications(2L)).thenReturn(List.of());

		mockMvc.perform(get("/notifications/me").header("Authorization", "Bearer user-token"))
			.andExpect(status().isOk());
	}

	@Configuration(proxyBeanMethods = false)
	@EnableWebMvc
	static class WebConfiguration {
	}
}
