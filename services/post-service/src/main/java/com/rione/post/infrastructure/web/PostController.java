package com.rione.post.infrastructure.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rione.post.application.port.in.PostService;
import com.rione.post.domain.model.PostType;
import com.rione.post.domain.model.PostVisibility;
import com.rione.post.domain.model.ReactionType;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping
class PostController {

	private final PostService postService;
	private final CurrentUser currentUser;

	PostController(PostService postService, CurrentUser currentUser) {
		this.postService = postService;
		this.currentUser = currentUser;
	}

	@GetMapping("/me/posts")
	@Operation(summary = "List my posts",
			description = "Only authenticated users can access this endpoint. It returns posts authored by the authenticated user, including private posts because the requester is the owner.")
	List<PostService.PostResponse> getMyPosts() {
		return postService.getMyPosts(currentUser.id());
	}

	@GetMapping("/posts")
	@Operation(summary = "List visible feed posts",
			description = "Only authenticated users can access this endpoint. It returns posts visible to the requester according to ownership, neighborhood, neighborship, block, and post privacy rules.")
	List<PostService.PostResponse> getVisiblePosts() {
		return postService.getVisiblePosts(currentUser.actor());
	}

	@GetMapping("/users/{authorId}/posts")
	@Operation(summary = "List visible posts by author",
			description = "Only authenticated users can access this endpoint. It returns the author's posts that the requester is authorized to view; private posts are included only for the author or authorized neighbours.")
	List<PostService.PostResponse> getPostsByAuthor(@PathVariable @Positive Long authorId) {
		return postService.getPostsByAuthor(authorId, currentUser.actor());
	}

	@GetMapping("/users/{authorId}/public-posts")
	@Operation(summary = "List public posts by author",
			description = "Only authenticated users can access this endpoint. It returns only public posts for the author and never exposes private posts or private-only post data.")
	List<PostService.PostResponse> getPublicPostsByAuthor(@PathVariable @Positive Long authorId) {
		return postService.getPublicPostsByAuthor(authorId);
	}

	@PostMapping("/posts")
	@Operation(summary = "Create a post",
			description = "Only authenticated users can access this endpoint. It creates a post owned by the authenticated user; future visibility is controlled by the selected post privacy.")
	ResponseEntity<PostService.PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
		PostService.PostResponse response = postService.createPost(new PostService.CreatePostCommand(currentUser.id(),
				request.neighborhoodId(), request.content(), postType(request.type()), visibility(request.visibility()),
				request.longitude(), request.latitude()));
		return ResponseEntity.created(URI.create("/posts/" + response.id())).body(response);
	}

	@GetMapping("/posts/{postId}")
	@Operation(summary = "Get a post",
			description = "Only authenticated users can access this endpoint. It returns the post only when the requester is authorized by ownership, neighborhood, neighborship, block, and privacy rules.")
	PostService.PostResponse getPost(@PathVariable @Positive Long postId) {
		return postService.getPost(postId, currentUser.actor());
	}

	@PatchMapping("/posts/{postId}")
	@Operation(summary = "Update a post",
			description = "Only authenticated users can access this endpoint. Only the owner of the post can edit it; the operation modifies post content state.")
	PostService.PostResponse updatePost(@PathVariable @Positive Long postId,
			@Valid @RequestBody UpdatePostRequest request) {
		return postService.updatePost(new PostService.UpdatePostCommand(postId, currentUser.id(), request.content()));
	}

	@DeleteMapping("/posts/{postId}")
	@Operation(summary = "Delete a post",
			description = "Only authenticated users can access this endpoint. Only the owner of the post can delete it; deleted posts are removed from visible post read models according to the event-sourced deletion strategy.")
	ResponseEntity<Void> deletePost(@PathVariable @Positive Long postId) {
		postService.deletePost(new PostService.DeletePostCommand(postId, currentUser.id()));
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/posts/{postId}/comments")
	@Operation(summary = "List comments",
			description = "Only authenticated users can access this endpoint. Comments are returned only when the requester is authorized to view the parent post.")
	List<PostService.CommentResponse> getComments(@PathVariable @Positive Long postId) {
		return postService.getComments(postId, currentUser.actor());
	}

	@PostMapping("/posts/{postId}/comments")
	@Operation(summary = "Add a comment",
			description = "Only authenticated users can access this endpoint. A user can comment only on posts they are authorized to view. Creating a comment records comment state and creates a notification for the post owner when the commenter is not the owner.")
	ResponseEntity<PostService.CommentResponse> addComment(@PathVariable @Positive Long postId,
			@Valid @RequestBody CommentRequest request) {
		PostService.CommentResponse response = postService
			.addComment(new PostService.AddCommentCommand(postId, currentUser.id(), request.content()));
		return ResponseEntity.created(URI.create("/posts/" + postId + "/comments/" + response.id())).body(response);
	}

	@PatchMapping("/posts/{postId}/comments/{commentId}")
	@Operation(summary = "Update a comment",
			description = "Only authenticated users can access this endpoint. Only the owner of the comment can edit it; the operation modifies comment state without changing the parent post visibility rules.")
	PostService.CommentResponse updateComment(@PathVariable @Positive Long postId,
			@PathVariable @Positive Long commentId, @Valid @RequestBody CommentRequest request) {
		return postService
			.updateComment(new PostService.UpdateCommentCommand(postId, commentId, currentUser.id(), request.content()));
	}

	@DeleteMapping("/posts/{postId}/comments/{commentId}")
	@Operation(summary = "Delete a comment",
			description = "Only authenticated users can access this endpoint. Only the owner of the comment can delete it unless broader moderator permissions are introduced later; deletion follows the service event-sourced comment removal strategy.")
	ResponseEntity<Void> deleteComment(@PathVariable @Positive Long postId, @PathVariable @Positive Long commentId) {
		postService.deleteComment(new PostService.DeleteCommentCommand(postId, commentId, currentUser.id()));
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/posts/{postId}/reactions")
	@Operation(summary = "List reactions",
			description = "Only authenticated users can access this endpoint. Reactions are returned only when the requester is authorized to view the parent post.")
	List<PostService.ReactionResponse> getReactions(@PathVariable @Positive Long postId) {
		return postService.getReactions(postId, currentUser.actor());
	}

	@PostMapping("/posts/{postId}/reactions")
	@Operation(summary = "Create or update my reaction",
			description = "Only authenticated users can access this endpoint. A user can react only to posts they are authorized to view; creating a new reaction creates a notification for the post owner when the reactor is not the owner.")
	PostService.ReactionResponse upsertReaction(@PathVariable @Positive Long postId,
			@Valid @RequestBody ReactionRequest request) {
		return postService
			.upsertReaction(new PostService.UpsertReactionCommand(postId, currentUser.id(), reactionType(request.type())));
	}

	@DeleteMapping("/posts/{postId}/reactions")
	@Operation(summary = "Delete my reaction",
			description = "Only authenticated users can access this endpoint. Users can delete only their own reaction from the post, modifying reaction state without affecting post content.")
	ResponseEntity<Void> deleteReaction(@PathVariable @Positive Long postId) {
		postService.deleteReaction(new PostService.DeleteReactionCommand(postId, currentUser.id()));
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/posts/{postId}/reactions/me")
	@Operation(summary = "Get my reaction",
			description = "Only authenticated users can access this endpoint. It returns the authenticated user's reaction only when the requester is authorized to view the parent post.")
	PostService.ReactionResponse getMyReaction(@PathVariable @Positive Long postId) {
		return postService.getMyReaction(postId, currentUser.actor());
	}

	private static PostType postType(String value) {
		return PostType.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
	}

	private static PostVisibility visibility(String value) {
		return PostVisibility.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
	}

	private static ReactionType reactionType(String value) {
		return ReactionType.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
	}

	record CreatePostRequest(@NotNull @Positive Long neighborhoodId, @NotBlank String content, @NotBlank String type,
			@NotBlank String visibility, Double longitude, Double latitude) {
	}

	record UpdatePostRequest(@NotBlank String content) {
	}

	record CommentRequest(@NotBlank String content) {
	}

	record ReactionRequest(@NotBlank String type) {
	}
}
