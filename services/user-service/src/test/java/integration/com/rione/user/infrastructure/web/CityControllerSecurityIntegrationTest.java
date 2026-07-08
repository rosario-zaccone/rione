package com.rione.user.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.rione.user.application.port.in.CityService;
import com.rione.user.application.port.in.CityService.CityResponse;

class CityControllerSecurityIntegrationTest {

	private AnnotationConfigWebApplicationContext context;
	private MockMvc mockMvc;
	private CityService cityService;
	private JwtService jwtService;
	private CurrentUser currentUser;

	@BeforeEach
	void setUp() {
		cityService = mock(CityService.class);
		jwtService = mock(JwtService.class);
		currentUser = mock(CurrentUser.class);
		when(currentUser.id()).thenReturn(1L);
		context = new AnnotationConfigWebApplicationContext();
		context.setServletContext(new org.springframework.mock.web.MockServletContext());
		context.register(WebConfiguration.class, SecurityConfig.class, JwtAuthenticationFilter.class,
				CityController.class);
		context.register((registry, environment) -> {
			registry.registerBean("cityService", CityService.class, spec -> spec.supplier(ignored -> cityService));
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
	void allowsPublicCityCatalogReads() throws Exception {
		when(cityService.listCities()).thenReturn(List.of(new CityResponse(10L, "Rome", List.of())));
		when(cityService.getCity(10L)).thenReturn(new CityResponse(10L, "Rome", List.of()));

		mockMvc.perform(get("/cities")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Rome"));
		mockMvc.perform(get("/cities/10")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Rome"));

		verify(cityService).listCities();
		verify(cityService).getCity(10L);
	}

	@Test
	void rejectsCityWriteOperationsForAuthenticatedNonAdmin() throws Exception {
		when(jwtService.parse("user-token")).thenReturn(new AuthenticatedPrincipal("2", false, false));

		mockMvc.perform(post("/cities").header("Authorization", "Bearer user-token")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"name\":\"Rome\",\"neighborhoods\":[\"Trastevere\"]}"))
			.andExpect(status().isForbidden());
		mockMvc.perform(delete("/cities/10").header("Authorization", "Bearer user-token"))
			.andExpect(status().isForbidden());
		mockMvc.perform(post("/cities/10/neighborhoods").header("Authorization", "Bearer user-token")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"name\":\"Trastevere\"}"))
			.andExpect(status().isForbidden());
	}

	@Test
	void allowsAdminToUseCityWriteOperations() throws Exception {
		when(jwtService.parse("admin-token")).thenReturn(new AuthenticatedPrincipal("1", true, false));
		when(cityService.createCity(any())).thenReturn(new CityResponse(10L, "Rome", List.of()));
		when(cityService.addNeighborhood(any()))
			.thenReturn(new CityResponse(10L, "Rome", List.of(new CityService.NeighborhoodResponse(20L, "Trastevere"))));

		mockMvc.perform(post("/cities").header("Authorization", "Bearer admin-token")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"name\":\"Rome\",\"neighborhoods\":[\"Trastevere\"]}"))
			.andExpect(status().isCreated());
		mockMvc.perform(post("/cities/10/neighborhoods").header("Authorization", "Bearer admin-token")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"name\":\"Trastevere\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.neighborhoods[0].name").value("Trastevere"));
		mockMvc.perform(delete("/cities/10").header("Authorization", "Bearer admin-token"))
			.andExpect(status().isNoContent());

		verify(cityService).createCity(any());
		verify(cityService).addNeighborhood(any());
		verify(cityService).removeCity(any());
	}

	@Configuration(proxyBeanMethods = false)
	@EnableWebMvc
	static class WebConfiguration {
	}
}
