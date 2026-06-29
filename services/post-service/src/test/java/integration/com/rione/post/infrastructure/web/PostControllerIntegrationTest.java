package com.rione.post.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

import com.rione.post.application.port.in.PostService;
import com.rione.post.domain.model.PostType;
import com.rione.post.domain.model.PostVisibility;
import com.rione.post.domain.model.ReactionType;

class PostControllerIntegrationTest {

	private MockMvc mockMvc;
	private PostService postService;
	private CurrentUser currentUser;

	@BeforeEach
	void setUp() {
		postService = org.mockito.Mockito.mock(PostService.class);
		currentUser = org.mockito.Mockito.mock(CurrentUser.class);
		when(currentUser.id()).thenReturn(10L);
		when(currentUser.actor()).thenReturn(new PostService.Actor(10L));
		mockMvc = MockMvcBuilders.standaloneSetup(new PostController(postService, currentUser), new PostExceptionHandler())
			.build();
	}

	@Test
	void createsPostThroughHttpContract() throws Exception {
		when(postService.createPost(any())).thenReturn(new PostService.PostResponse(1L, 10L, 5L,
				"Neighborhood discussion content", "DISCUSSION", "PUBLIC", null, 0,
				LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 0), null));

		mockMvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON)
			.content("""
					{"neighborhoodId":5,"content":"Neighborhood discussion content","type":"discussion","visibility":"public"}
					"""))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "/posts/1"))
			.andExpect(jsonPath("$.authorId").value(10L));

		ArgumentCaptor<PostService.CreatePostCommand> command = ArgumentCaptor
			.forClass(PostService.CreatePostCommand.class);
		verify(postService).createPost(command.capture());
		org.assertj.core.api.Assertions.assertThat(command.getValue())
			.extracting(PostService.CreatePostCommand::authorId, PostService.CreatePostCommand::neighborhoodId,
					PostService.CreatePostCommand::type, PostService.CreatePostCommand::visibility)
			.containsExactly(10L, 5L, PostType.DISCUSSION, PostVisibility.PUBLIC);
	}

	@Test
	void addsCommentThroughHttpContract() throws Exception {
		when(postService.addComment(any())).thenReturn(new PostService.CommentResponse(3L, 1L, 10L,
				"This comment is long enough", LocalDateTime.now(), LocalDateTime.now(), null));

		mockMvc.perform(post("/posts/1/comments").contentType(MediaType.APPLICATION_JSON)
			.content("{\"content\":\"This comment is long enough\"}"))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "/posts/1/comments/3"))
			.andExpect(jsonPath("$.id").value(3L));
	}

	@Test
	void updatesCommentThroughHttpContract() throws Exception {
		when(postService.updateComment(any())).thenReturn(new PostService.CommentResponse(3L, 1L, 10L,
				"Updated comment is long enough", LocalDateTime.now(), LocalDateTime.now(), null));

		mockMvc.perform(patch("/posts/1/comments/3").contentType(MediaType.APPLICATION_JSON)
			.content("{\"content\":\"Updated comment is long enough\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("Updated comment is long enough"));

		verify(postService).updateComment(new PostService.UpdateCommentCommand(1L, 3L, 10L,
				"Updated comment is long enough"));
	}

	@Test
	void deletesCommentThroughHttpContract() throws Exception {
		mockMvc.perform(delete("/posts/1/comments/3")).andExpect(status().isNoContent());

		verify(postService).deleteComment(new PostService.DeleteCommentCommand(1L, 3L, 10L));
	}

	@Test
	void upsertsReactionThroughHttpContract() throws Exception {
		when(postService.upsertReaction(any()))
			.thenReturn(new PostService.ReactionResponse(1L, 10L, "UPVOTE", LocalDateTime.now(), LocalDateTime.now(),
					null));

		mockMvc.perform(post("/posts/1/reactions").contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"upvote\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.type").value("UPVOTE"));

		verify(postService).upsertReaction(new PostService.UpsertReactionCommand(1L, 10L, ReactionType.UPVOTE));
	}

	@Test
	void deletesPostThroughHttpContract() throws Exception {
		mockMvc.perform(delete("/posts/1")).andExpect(status().isNoContent());

		verify(postService).deletePost(new PostService.DeletePostCommand(1L, 10L));
	}

	@Test
	void rejectsInvalidPostRequest() throws Exception {
		mockMvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content("{}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.detail").value("Request validation failed"));
	}

	@Test
	void listsOwnPostsThroughHttpContract() throws Exception {
		when(postService.getMyPosts(10L)).thenReturn(List.of(new PostService.PostResponse(1L, 10L, 5L,
				"Neighborhood discussion content", "DISCUSSION", "PUBLIC", null, 0,
				LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 0), null)));

		mockMvc.perform(get("/me/posts")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1L));
	}

	@Test
	void listsVisiblePostsThroughHttpContract() throws Exception {
		when(postService.getVisiblePosts(new PostService.Actor(10L)))
			.thenReturn(List.of(new PostService.PostResponse(1L, 20L, 5L, "Neighborhood discussion content",
					"DISCUSSION", "PUBLIC", null, 0, LocalDateTime.of(2026, 1, 1, 10, 0),
					LocalDateTime.of(2026, 1, 1, 10, 0), null)));

		mockMvc.perform(get("/posts")).andExpect(status().isOk()).andExpect(jsonPath("$[0].authorId").value(20L));
	}

	@Test
	void listsSpecificAuthorPostsThroughHttpContract() throws Exception {
		when(postService.getPostsByAuthor(20L, new PostService.Actor(10L)))
			.thenReturn(List.of(new PostService.PostResponse(1L, 20L, 5L, "Neighborhood discussion content",
					"DISCUSSION", "PUBLIC", null, 0, LocalDateTime.of(2026, 1, 1, 10, 0),
					LocalDateTime.of(2026, 1, 1, 10, 0), null)));

		mockMvc.perform(get("/users/20/posts")).andExpect(status().isOk()).andExpect(jsonPath("$[0].authorId").value(20L));
	}

	@Test
	void listsPublicAuthorPostsThroughHttpContract() throws Exception {
		when(postService.getPublicPostsByAuthor(20L))
			.thenReturn(List.of(new PostService.PostResponse(1L, 20L, 5L, "Public discussion content",
					"DISCUSSION", "PUBLIC", null, 0, LocalDateTime.of(2026, 1, 1, 10, 0),
					LocalDateTime.of(2026, 1, 1, 10, 0), null)));

		mockMvc.perform(get("/users/20/public-posts"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].visibility").value("PUBLIC"));
	}
}
