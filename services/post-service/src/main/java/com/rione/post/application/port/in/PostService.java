package com.rione.post.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import com.rione.common.application.InPort;
import com.rione.post.domain.model.PostType;
import com.rione.post.domain.model.PostVisibility;
import com.rione.post.domain.model.ReactionType;

@InPort
public interface PostService {

	List<PostResponse> getMyPosts(Long userId);

	List<PostResponse> getVisiblePosts(Actor actor);

	List<PostResponse> getPostsByAuthor(Long authorId, Actor actor);

	List<PostResponse> getPublicPostsByAuthor(Long authorId);

	PostResponse createPost(CreatePostCommand command);

	PostResponse getPost(Long postId, Actor actor);

	PostResponse updatePost(UpdatePostCommand command);

	void deletePost(DeletePostCommand command);

	List<CommentResponse> getComments(Long postId, Actor actor);

	CommentResponse addComment(AddCommentCommand command);

	CommentResponse updateComment(UpdateCommentCommand command);

	void deleteComment(DeleteCommentCommand command);

	List<ReactionResponse> getReactions(Long postId, Actor actor);

	ReactionResponse upsertReaction(UpsertReactionCommand command);

	void deleteReaction(DeleteReactionCommand command);

	ReactionResponse getMyReaction(Long postId, Actor actor);

	record Actor(Long userId) {
	}

	record CreatePostCommand(Long authorId, Long neighborhoodId, String content, PostType type,
			PostVisibility visibility, Double longitude, Double latitude) {
	}

	record UpdatePostCommand(Long postId, Long actorId, String content) {
	}

	record DeletePostCommand(Long postId, Long actorId) {
	}

	record AddCommentCommand(Long postId, Long authorId, String content) {
	}

	record UpdateCommentCommand(Long postId, Long commentId, Long actorId, String content) {
	}

	record DeleteCommentCommand(Long postId, Long commentId, Long actorId) {
	}

	record UpsertReactionCommand(Long postId, Long actorId, ReactionType type) {
	}

	record DeleteReactionCommand(Long postId, Long actorId) {
	}

	record PlaceResponse(Double longitude, Double latitude) {
	}

	record UserProfileResponse(Long id, String name, String surname, String username) {
	}

	record PostResponse(Long id, Long authorId, Long neighborhoodId, String content, String type, String visibility,
			PlaceResponse place, int score, LocalDateTime createdAt, LocalDateTime updatedAt,
			UserProfileResponse author) {
	}

	record CommentResponse(Long id, Long postId, Long authorId, String content, LocalDateTime createdAt,
			LocalDateTime updatedAt, UserProfileResponse author) {
	}

	record ReactionResponse(Long postId, Long userId, String type, LocalDateTime createdAt, LocalDateTime updatedAt,
			UserProfileResponse user) {
	}
}
