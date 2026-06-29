package com.rione.user.infrastructure.web;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

import com.rione.user.application.port.in.UserService;
import com.rione.user.application.port.in.UserService.UserDirectoryResponse;
import com.rione.user.application.port.in.UserService.UserNeighborhoodResponse;

class InternalUserControllerSecurityIntegrationTest {

	private AnnotationConfigWebApplicationContext context;
	private MockMvc mockMvc;
	private UserService userService;
	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		jwtService = mock(JwtService.class);
		context = new AnnotationConfigWebApplicationContext();
		context.setServletContext(new org.springframework.mock.web.MockServletContext());
		context.register(WebConfiguration.class, SecurityConfig.class, JwtAuthenticationFilter.class,
				InternalUserController.class);
		context.register((registry, environment) -> {
			registry.registerBean("userService", UserService.class, spec -> spec.supplier(ignored -> userService));
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
	void rejectsMissingServiceToken() throws Exception {
		mockMvc.perform(get("/internal/users/2/neighborhood")).andExpect(status().isUnauthorized());
	}

	@Test
	void rejectsUserAndAdminTokens() throws Exception {
		when(jwtService.parse("user-token")).thenReturn(new AuthenticatedPrincipal("2", false, false));
		when(jwtService.parse("admin-token")).thenReturn(new AuthenticatedPrincipal("1", true, false));

		mockMvc.perform(get("/internal/users/2/neighborhood").header("Authorization", "Bearer user-token"))
			.andExpect(status().isForbidden());
		mockMvc.perform(get("/internal/users/2/neighborhood").header("Authorization", "Bearer admin-token"))
			.andExpect(status().isForbidden());
	}

	@Test
	void returnsOnlyNeighborhoodDataForServiceToken() throws Exception {
		when(jwtService.parse("service-token"))
			.thenReturn(new AuthenticatedPrincipal("social-service", false, true));
		when(userService.getUserNeighborhood(2L)).thenReturn(new UserNeighborhoodResponse(2L, 10L));

		mockMvc.perform(get("/internal/users/2/neighborhood").header("Authorization", "Bearer service-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(2L))
			.andExpect(jsonPath("$.neighborhoodId").value(10L))
			.andExpect(jsonPath("$.mail").doesNotExist())
			.andExpect(jsonPath("$.admin").doesNotExist());

		verify(userService).getUserNeighborhood(2L);
	}

	@Test
	void searchesMinimalUserProfilesForServiceToken() throws Exception {
		when(jwtService.parse("service-token"))
			.thenReturn(new AuthenticatedPrincipal("social-service", false, true));
		when(userService.searchUsers(10L, "ada"))
			.thenReturn(List.of(new UserDirectoryResponse(2L, "Ada", "Lovelace", "ada")));

		mockMvc.perform(get("/internal/users/search").param("neighborhoodId", "10").param("query", "ada")
			.header("Authorization", "Bearer service-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].username").value("ada"))
			.andExpect(jsonPath("$[0].mail").doesNotExist())
			.andExpect(jsonPath("$[0].bio").doesNotExist());
	}

	@Test
	void resolvesMinimalUserProfilesForServiceToken() throws Exception {
		when(jwtService.parse("service-token"))
			.thenReturn(new AuthenticatedPrincipal("social-service", false, true));
		when(userService.getUserProfiles(List.of(2L, 3L)))
			.thenReturn(List.of(new UserDirectoryResponse(2L, "Ada", "Lovelace", "ada"),
					new UserDirectoryResponse(3L, "Grace", "Hopper", "grace")));

		mockMvc.perform(get("/internal/users/profiles").param("ids", "2", "3")
			.header("Authorization", "Bearer service-token"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].username").value("ada"))
			.andExpect(jsonPath("$[1].username").value("grace"))
			.andExpect(jsonPath("$[0].mail").doesNotExist())
			.andExpect(jsonPath("$[0].bio").doesNotExist());

		verify(userService).getUserProfiles(List.of(2L, 3L));
	}

	@Configuration(proxyBeanMethods = false)
	@EnableWebMvc
	static class WebConfiguration {
	}
}
