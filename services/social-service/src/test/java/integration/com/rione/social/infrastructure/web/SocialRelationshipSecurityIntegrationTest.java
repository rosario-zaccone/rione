package com.rione.social.infrastructure.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.rione.social.application.port.in.SocialService;
import com.rione.social.application.port.in.SocialService.BlockOperationResult;
import com.rione.social.application.port.in.SocialService.BlockResponse;
import com.rione.social.infrastructure.config.JwtService;

class SocialRelationshipSecurityIntegrationTest {

	private AnnotationConfigWebApplicationContext context;
	private MockMvc mockMvc;
	private SocialService socialService;
	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		socialService = mock(SocialService.class);
		jwtService = mock(JwtService.class);
		CurrentUser currentUser = mock(CurrentUser.class);
		when(currentUser.id()).thenReturn(10L);
		when(socialService.putBlock(org.mockito.ArgumentMatchers.any()))
			.thenReturn(new BlockOperationResult(new BlockResponse(1L, 10L, 20L, null), true));
		context = new AnnotationConfigWebApplicationContext();
		context.setServletContext(new org.springframework.mock.web.MockServletContext());
		context.register(WebConfiguration.class, SecurityConfig.class, JwtAuthenticationFilter.class,
				SocialController.class);
		context.register((registry, environment) -> {
			registry.registerBean("socialService", SocialService.class, spec -> spec.supplier(ignored -> socialService));
			registry.registerBean("jwtService", JwtService.class, spec -> spec.supplier(ignored -> jwtService));
			registry.registerBean("currentUser", CurrentUser.class, spec -> spec.supplier(ignored -> currentUser));
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
	void requiresAuthenticationForRelationshipMutations() throws Exception {
		mockMvc.perform(delete("/me/neighborships/20")).andExpect(status().isUnauthorized());
		mockMvc.perform(put("/me/blocks/20")).andExpect(status().isUnauthorized());
		mockMvc.perform(delete("/me/blocks/20")).andExpect(status().isUnauthorized());
	}

	@Test
	void acceptsAuthenticatedUserForOwnRelationshipMutations() throws Exception {
		when(jwtService.parse("user-token")).thenReturn(new AuthenticatedPrincipal(10L, false));

		mockMvc.perform(delete("/me/neighborships/20").header("Authorization", "Bearer user-token"))
			.andExpect(status().isNoContent());
		mockMvc.perform(put("/me/blocks/20").header("Authorization", "Bearer user-token"))
			.andExpect(status().isCreated());
		mockMvc.perform(delete("/me/blocks/20").header("Authorization", "Bearer user-token"))
			.andExpect(status().isNoContent());
	}

	@Configuration(proxyBeanMethods = false)
	@EnableWebMvc
	static class WebConfiguration {
	}
}
