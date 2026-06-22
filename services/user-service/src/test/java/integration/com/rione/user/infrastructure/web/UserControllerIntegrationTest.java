package com.rione.user.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rione.user.application.port.in.UserService;
import com.rione.user.application.port.in.UserService.UserResponse;

class UserControllerIntegrationTest {

	private MockMvc mockMvc;
	private UserService userService;
	private JwtService jwtService;
	private CurrentUser currentUser;

	@BeforeEach
	void setUp() {
		userService = org.mockito.Mockito.mock(UserService.class);
		jwtService = org.mockito.Mockito.mock(JwtService.class);
		currentUser = org.mockito.Mockito.mock(CurrentUser.class);
		when(currentUser.id()).thenReturn(1L);
		mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService, jwtService, currentUser),
				new UserExceptionHandler())
			.build();
	}

	@Test
	void createsUserThroughHttpContract() throws Exception {
		when(userService.signUp(any())).thenReturn(response());

		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("""
				{
				  "name": "Ada",
				  "surname": "Lovelace",
				  "username": "ada",
				  "mail": "ada@rione.test",
				  "password": "secret-password",
				  "neighborhoodId": 10,
				  "birthDate": "1990-01-01T00:00:00",
				  "bio": "I enjoy helping neighbors solve local problems."
				}
				"""))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "/users/1"))
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.username").value("ada"));

		verify(userService).signUp(any());
	}

	@Test
	void logsInThroughHttpContract() throws Exception {
		when(userService.logIn(any())).thenReturn(response());
		when(jwtService.createToken(1L, false)).thenReturn("jwt-token");

		mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
			.content("{\"mail\":\"ada@rione.test\",\"password\":\"secret-password\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value("jwt-token"))
			.andExpect(jsonPath("$.user.mail").value("ada@rione.test"));

		verify(userService).logIn(any());
		verify(jwtService).createToken(1L, false);
	}

	@Test
	void logsOutThroughHttpContract() throws Exception {
		when(currentUser.token()).thenReturn("jwt-token");

		mockMvc.perform(post("/users/logout")).andExpect(status().isNoContent());

		verify(jwtService).revoke("jwt-token");
	}

	@Test
	void getsCurrentUserThroughMeEndpoint() throws Exception {
		when(userService.getUser(1L)).thenReturn(response());

		mockMvc.perform(get("/users/me")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L));

		verify(userService).getUser(1L);
	}

	@Test
	void updatesCurrentUserProfileThroughMeEndpoint() throws Exception {
		when(userService.updateProfile(any())).thenReturn(response());

		mockMvc.perform(put("/users/me/profile").contentType(MediaType.APPLICATION_JSON).content("""
				{
				  "name": "Ada",
				  "surname": "Lovelace",
				  "username": "ada",
				  "neighborhoodId": 10,
				  "birthDate": "1990-01-01T00:00:00",
				  "bio": "I enjoy helping neighbors solve local problems."
				}
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("ada"));

		verify(userService).updateProfile(any());
	}

	@Test
	void removedCrossUserRoutesReturnNotFound() throws Exception {
		mockMvc.perform(get("/users/2")).andExpect(status().isNotFound());
		mockMvc.perform(put("/users/2/profile").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isNotFound());
	}

	@Test
	void rejectsInvalidSignUpRequest() throws Exception {
		mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.detail").value("Request validation failed"));
	}

	private static UserResponse response() {
		return new UserResponse(1L, "Ada", "Lovelace", "ada", "ada@rione.test", 10L,
				LocalDateTime.of(1990, 1, 1, 0, 0), "I enjoy helping neighbors solve local problems.", false);
	}
}
