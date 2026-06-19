package com.rione.social.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rione.social.application.port.in.SocialService;
import com.rione.social.application.port.in.SocialService.BlockResponse;
import com.rione.social.application.port.in.SocialService.NeighborRequestResponse;

class SocialControllerIntegrationTest {

	private MockMvc mockMvc;
	private SocialService socialService;

	@BeforeEach
	void setUp() {
		socialService = org.mockito.Mockito.mock(SocialService.class);
		mockMvc = MockMvcBuilders.standaloneSetup(new SocialController(socialService), new SocialExceptionHandler())
			.build();
	}

	@Test
	void createsNeighborRequestThroughHttpContract() throws Exception {
		when(socialService.sendNeighborRequest(any()))
			.thenReturn(new NeighborRequestResponse(1L, 10L, 20L, LocalDateTime.of(2026, 1, 1, 0, 0), "PENDING"));

		mockMvc.perform(post("/neighbor-requests").contentType(MediaType.APPLICATION_JSON)
			.content("{\"senderId\":10,\"receiverId\":20}"))
			.andExpect(status().isCreated())
			.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
				.string("Location", "/neighbor-requests/1"))
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.status").value("PENDING"));

		verify(socialService).sendNeighborRequest(any());
	}

	@Test
	void blocksUserThroughHttpContract() throws Exception {
		when(socialService.blockUser(any())).thenReturn(new BlockResponse(1L, 10L, 20L));

		mockMvc.perform(post("/blocks").contentType(MediaType.APPLICATION_JSON)
			.content("{\"blockerId\":10,\"blockedId\":20}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.blockerId").value(10L))
			.andExpect(jsonPath("$.blockedId").value(20L));

		verify(socialService).blockUser(any());
	}

	@Test
	void unblocksUserThroughHttpContract() throws Exception {
		mockMvc.perform(post("/blocks/removal").contentType(MediaType.APPLICATION_JSON)
			.content("{\"blockerId\":10,\"blockedId\":20}"))
			.andExpect(status().isNoContent());

		verify(socialService).unblockUser(any());
	}

	@Test
	void reconcilesNeighborhoodChangeThroughHttpContract() throws Exception {
		mockMvc.perform(post("/neighborhood-changes").contentType(MediaType.APPLICATION_JSON)
			.content("{\"userId\":10}"))
			.andExpect(status().isAccepted());

		verify(socialService).reconcileRelationshipsAfterNeighborhoodChange(any());
	}

	@Test
	void rejectsInvalidNeighborRequest() throws Exception {
		mockMvc.perform(post("/neighbor-requests").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.detail").value("Request validation failed"));
	}
}
