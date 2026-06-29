package com.rione.social.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rione.social.application.port.in.SocialService;
import com.rione.social.application.port.in.SocialService.BlockResponse;
import com.rione.social.application.port.in.SocialService.BlockOperationResult;
import com.rione.social.application.port.in.SocialService.NeighborRequestResponse;
import com.rione.social.application.port.in.SocialService.NeighborResponse;
import com.rione.social.application.port.in.SocialService.SendNeighborRequestCommand;
import com.rione.social.application.port.in.SocialService.UnblockUserCommand;

class SocialControllerIntegrationTest {

	private MockMvc mockMvc;
	private SocialService socialService;
	private CurrentUser currentUser;

	@BeforeEach
	void setUp() {
		socialService = org.mockito.Mockito.mock(SocialService.class);
		currentUser = org.mockito.Mockito.mock(CurrentUser.class);
		when(currentUser.id()).thenReturn(10L);
		when(currentUser.actor()).thenReturn(new SocialService.Actor(10L));
		mockMvc = MockMvcBuilders.standaloneSetup(new SocialController(socialService, currentUser),
				new SocialExceptionHandler())
			.build();
	}

	@Test
	void createsNeighborRequestThroughHttpContract() throws Exception {
		when(socialService.sendNeighborRequest(any()))
			.thenReturn(new NeighborRequestResponse(1L, 10L, 20L, LocalDateTime.of(2026, 1, 1, 0, 0), "PENDING",
					null, null));

		mockMvc.perform(post("/neighbor-requests").contentType(MediaType.APPLICATION_JSON)
			.content("{\"receiverId\":20}"))
			.andExpect(status().isCreated())
			.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
				.string("Location", "/neighbor-requests/1"))
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.status").value("PENDING"));

		ArgumentCaptor<SendNeighborRequestCommand> command = ArgumentCaptor.forClass(SendNeighborRequestCommand.class);
		verify(socialService).sendNeighborRequest(command.capture());
		org.assertj.core.api.Assertions.assertThat(command.getValue())
			.extracting(SendNeighborRequestCommand::senderId, SendNeighborRequestCommand::receiverId)
			.containsExactly(10L, 20L);
	}

	@Test
	void listsCurrentUsersSentNeighborRequestsThroughHttpContract() throws Exception {
		when(socialService.getSentNeighborRequests(10L))
			.thenReturn(List.of(new NeighborRequestResponse(1L, 10L, 20L, LocalDateTime.of(2026, 1, 1, 0, 0),
					"PENDING", null, new SocialService.UserProfileResponse(20L, "Ada", "Lovelace", "ada"))));

		mockMvc.perform(get("/me/neighbor-requests/sent"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].senderId").value(10L))
			.andExpect(jsonPath("$[0].receiverId").value(20L))
			.andExpect(jsonPath("$[0].status").value("PENDING"));

		verify(socialService).getSentNeighborRequests(10L);
	}

	@Test
	void searchesUsersThroughHttpContract() throws Exception {
		when(socialService.searchUsers(any())).thenReturn(List.of(
				new SocialService.UserSearchResponse(20L, "Ada", "Lovelace", "ada")));

		mockMvc.perform(get("/users").param("query", "ada"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(20L))
			.andExpect(jsonPath("$[0].username").value("ada"));

		verify(socialService).searchUsers(new SocialService.SearchUsersQuery(10L, "ada"));
	}

	@Test
	void listsCurrentUsersReceivedNeighborRequestsThroughHttpContract() throws Exception {
		when(socialService.getReceivedNeighborRequests(10L))
			.thenReturn(List.of(new NeighborRequestResponse(1L, 20L, 10L, LocalDateTime.of(2026, 1, 1, 0, 0),
					"PENDING", new SocialService.UserProfileResponse(20L, "Ada", "Lovelace", "ada"), null)));

		mockMvc.perform(get("/me/neighbor-requests/received"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].senderId").value(20L))
			.andExpect(jsonPath("$[0].receiverId").value(10L))
			.andExpect(jsonPath("$[0].status").value("PENDING"));

		verify(socialService).getReceivedNeighborRequests(10L);
	}

	@Test
	void oldUserScopedAndRequestByIdRoutesNoLongerExist() throws Exception {
		mockMvc.perform(get("/neighbor-requests/users/20/sent")).andExpect(status().isNotFound());
		mockMvc.perform(get("/neighbor-requests/99")).andExpect(status().isNotFound());
		mockMvc.perform(post("/blocks").contentType(MediaType.APPLICATION_JSON).content("{\"blockedId\":20}"))
			.andExpect(status().isNotFound());
		mockMvc.perform(post("/blocks/removal").contentType(MediaType.APPLICATION_JSON)
			.content("{\"blockedId\":20}"))
			.andExpect(status().isNotFound());
	}

	@Test
	void listsBlockedUsersThroughHttpContract() throws Exception {
		when(socialService.getBlocks(10L)).thenReturn(List.of(new BlockResponse(1L, 10L, 20L,
				new SocialService.UserProfileResponse(20L, "Ada", "Lovelace", "ada"))));

		mockMvc.perform(get("/me/blocks"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].blockerId").value(10L))
			.andExpect(jsonPath("$[0].blockedId").value(20L));

		verify(socialService).getBlocks(10L);
	}

	@Test
	void listsCurrentUsersNeighborsThroughHttpContract() throws Exception {
		when(socialService.getNeighbors(10L))
			.thenReturn(List.of(new NeighborResponse(1L, 10L, 20L, LocalDateTime.of(2026, 1, 1, 0, 0),
					new SocialService.UserProfileResponse(20L, "Ada", "Lovelace", "ada"))));

		mockMvc.perform(get("/me/neighborships"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].userId").value(10L))
			.andExpect(jsonPath("$[0].neighborId").value(20L));

		verify(socialService).getNeighbors(10L);
	}

	@Test
	void removesCurrentUsersNeighborshipThroughHttpContract() throws Exception {
		mockMvc.perform(delete("/me/neighborships/20")).andExpect(status().isNoContent());

		verify(socialService).removeNeighborship(new SocialService.RemoveNeighborshipCommand(10L, 20L));
	}

	@Test
	void returnsNotFoundWhenNeighborshipDoesNotExist() throws Exception {
		org.mockito.Mockito.doThrow(new com.rione.social.application.service.SocialNotFoundException("Neighborship not found"))
			.when(socialService)
			.removeNeighborship(any());

		mockMvc.perform(delete("/me/neighborships/20"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.detail").value("Neighborship not found"));
	}

	@Test
	void createsBlockThroughRestfulEndpoint() throws Exception {
		when(socialService.putBlock(any()))
			.thenReturn(new BlockOperationResult(new BlockResponse(1L, 10L, 20L, null), true));

		mockMvc.perform(put("/me/blocks/20")).andExpect(status().isCreated())
			.andExpect(jsonPath("$.blockedId").value(20L));
	}

	@Test
	void returnsExistingBlockThroughIdempotentPut() throws Exception {
		when(socialService.putBlock(any()))
			.thenReturn(new BlockOperationResult(new BlockResponse(1L, 10L, 20L, null), false));

		mockMvc.perform(put("/me/blocks/20")).andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L));
	}

	@Test
	void deletesBlockIdempotentlyThroughRestfulEndpoint() throws Exception {
		mockMvc.perform(delete("/me/blocks/20")).andExpect(status().isNoContent());

		verify(socialService).unblockUser(new UnblockUserCommand(10L, 20L));
	}

	@Test
	void removedAdminRoutesReturnNotFound() throws Exception {
		mockMvc.perform(get("/admin/users/20/blocks")).andExpect(status().isNotFound());
		mockMvc.perform(post("/neighborhood-changes").contentType(MediaType.APPLICATION_JSON)
			.content("{\"userId\":10}"))
			.andExpect(status().isNotFound());
	}

	@Test
	void rejectsInvalidNeighborRequest() throws Exception {
		mockMvc.perform(post("/neighbor-requests").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.detail").value("Request validation failed"));
	}
}
