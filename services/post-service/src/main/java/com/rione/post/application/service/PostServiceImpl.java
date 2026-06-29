package com.rione.post.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rione.post.application.port.in.PostService;
import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.application.port.out.PostNotificationPublisher;
import com.rione.post.application.port.out.PostUserDirectory;
import com.rione.post.application.port.out.PostVisibilityChecker;
import com.rione.post.domain.event.CommentAdded;
import com.rione.post.domain.event.CommentRemoved;
import com.rione.post.domain.event.CommentUpdated;
import com.rione.post.domain.event.PostContentUpdated;
import com.rione.post.domain.event.PostCreated;
import com.rione.post.domain.event.PostDeleted;
import com.rione.post.domain.event.PostEvent;
import com.rione.post.domain.event.ReactionAdded;
import com.rione.post.domain.event.ReactionRemoved;
import com.rione.post.domain.event.ReactionUpdated;
import com.rione.post.domain.model.CommentContent;
import com.rione.post.domain.model.CommentId;
import com.rione.post.domain.model.NeighborhoodId;
import com.rione.post.domain.model.Place;
import com.rione.post.domain.model.PostContent;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.PostVisibility;
import com.rione.post.domain.model.ReactionId;
import com.rione.post.domain.model.ReactionType;
import com.rione.post.domain.model.UserId;

@Service
public class PostServiceImpl implements PostService {

	private final PostEventStore eventStore;
	private final PostNotificationPublisher notificationPublisher;
	private final PostVisibilityChecker visibilityChecker;
	private final PostUserDirectory userDirectory;

	public PostServiceImpl(PostEventStore eventStore, PostNotificationPublisher notificationPublisher,
			PostVisibilityChecker visibilityChecker, PostUserDirectory userDirectory) {
		this.eventStore = eventStore;
		this.notificationPublisher = notificationPublisher;
		this.visibilityChecker = visibilityChecker;
		this.userDirectory = userDirectory;
	}

	@Override
	public List<PostResponse> getMyPosts(Long userId) {
		UserId author = new UserId(userId);
		return statesFromAllEvents().stream()
			.filter(state -> !state.deleted)
			.filter(state -> state.author.equals(author))
			.map(this::toResponse)
			.toList();
	}

	@Override
	public List<PostResponse> getVisiblePosts(Actor actor) {
		UserId viewer = new UserId(actor.userId());
		return statesFromAllEvents().stream()
			.filter(state -> !state.deleted)
			.filter(state -> canView(state, viewer))
			.sorted(Comparator.comparing((PostState state) -> state.createdAt).reversed())
			.map(this::toResponse)
			.toList();
	}

	@Override
	public List<PostResponse> getPostsByAuthor(Long authorId, Actor actor) {
		UserId author = new UserId(authorId);
		UserId viewer = new UserId(actor.userId());
		if (author.equals(viewer)) {
			return getMyPosts(viewer.value());
		}
		PostVisibilityChecker.RelationshipVisibility relationship = visibilityChecker.visibilityBetween(viewer, author);
		if (relationship.blocked() || !relationship.sameNeighborhood()) {
			return List.of();
		}
		return statesFromAllEvents().stream()
			.filter(state -> !state.deleted)
			.filter(state -> state.author.equals(author))
			.filter(state -> state.visibility == PostVisibility.PUBLIC || relationship.activeNeighborship())
			.map(this::toResponse)
			.toList();
	}

	@Override
	public List<PostResponse> getPublicPostsByAuthor(Long authorId) {
		UserId author = new UserId(authorId);
		return statesFromAllEvents().stream()
			.filter(state -> !state.deleted)
			.filter(state -> state.author.equals(author))
			.filter(state -> state.visibility == PostVisibility.PUBLIC)
			.sorted(Comparator.comparing((PostState state) -> state.createdAt).reversed())
			.map(this::toResponse)
			.toList();
	}

	@Override
	@Transactional
	public PostResponse createPost(CreatePostCommand command) {
		PostId postId = eventStore.nextPostId();
		Place place = command.longitude() == null && command.latitude() == null ? null
				: new Place(command.longitude(), command.latitude());
		PostCreated event = new PostCreated(postId, new UserId(command.authorId()),
				new NeighborhoodId(command.neighborhoodId()), new PostContent(command.content()), place, command.type(),
				command.visibility(), LocalDateTime.now());
		eventStore.append(postId, 0, List.of(event));
		return toResponse(stateOf(postId));
	}

	@Override
	public PostResponse getPost(Long postId, Actor actor) {
		PostState state = visibleState(new PostId(postId), new UserId(actor.userId()));
		return toResponse(state);
	}

	@Override
	@Transactional
	public PostResponse updatePost(UpdatePostCommand command) {
		PostId postId = new PostId(command.postId());
		PostState state = existingState(postId);
		requireAuthor(state, new UserId(command.actorId()), "update this post");
		PostContentUpdated event = new PostContentUpdated(postId, state.author, new PostContent(command.content()),
				LocalDateTime.now());
		eventStore.append(postId, state.version, List.of(event));
		return toResponse(stateOf(postId));
	}

	@Override
	@Transactional
	public void deletePost(DeletePostCommand command) {
		PostId postId = new PostId(command.postId());
		PostState state = existingState(postId);
		requireAuthor(state, new UserId(command.actorId()), "delete this post");
		eventStore.append(postId, state.version, List.of(new PostDeleted(postId, state.author, LocalDateTime.now())));
	}

	@Override
	public List<CommentResponse> getComments(Long postId, Actor actor) {
		return visibleState(new PostId(postId), new UserId(actor.userId())).comments.values()
			.stream()
			.map(comment -> comment.toResponse(postId, profileOf(comment.author)))
			.toList();
	}

	@Override
	@Transactional
	public CommentResponse addComment(AddCommentCommand command) {
		PostId postId = new PostId(command.postId());
		PostState state = visibleState(postId, new UserId(command.authorId()));
		CommentAdded event = new CommentAdded(postId, eventStore.nextCommentId(), new UserId(command.authorId()),
				new CommentContent(command.content()), LocalDateTime.now());
		eventStore.append(postId, state.version, List.of(event));
		notificationPublisher.publishCommentAdded(event, state.author);
		CommentState comment = stateOf(postId).comments.get(event.commentId());
		return comment.toResponse(postId.value(), profileOf(comment.author));
	}

	@Override
	@Transactional
	public CommentResponse updateComment(UpdateCommentCommand command) {
		PostId postId = new PostId(command.postId());
		PostState state = existingState(postId);
		CommentState comment = state.comment(new CommentId(command.commentId()));
		requireUser(comment.author, new UserId(command.actorId()), "update this comment");
		CommentUpdated event = new CommentUpdated(postId, comment.id, comment.author, new CommentContent(command.content()),
				LocalDateTime.now());
		eventStore.append(postId, state.version, List.of(event));
		CommentState updated = stateOf(postId).comment(comment.id);
		return updated.toResponse(postId.value(), profileOf(updated.author));
	}

	@Override
	@Transactional
	public void deleteComment(DeleteCommentCommand command) {
		PostId postId = new PostId(command.postId());
		PostState state = existingState(postId);
		CommentState comment = state.comment(new CommentId(command.commentId()));
		requireUser(comment.author, new UserId(command.actorId()), "delete this comment");
		eventStore.append(postId, state.version,
				List.of(new CommentRemoved(postId, comment.id, comment.author, LocalDateTime.now())));
	}

	@Override
	public List<ReactionResponse> getReactions(Long postId, Actor actor) {
		return visibleState(new PostId(postId), new UserId(actor.userId())).reactions.values()
			.stream()
			.map(reaction -> reaction.toResponse(postId, profileOf(reaction.author)))
			.toList();
	}

	@Override
	@Transactional
	public ReactionResponse upsertReaction(UpsertReactionCommand command) {
		PostId postId = new PostId(command.postId());
		PostState state = visibleState(postId, new UserId(command.actorId()));
		UserId actor = new UserId(command.actorId());
		PostEvent event;
		if (state.reactions.containsKey(actor)) {
			ReactionState reaction = state.reactions.get(actor);
			event = new ReactionUpdated(postId, reaction.id, actor, command.type(), LocalDateTime.now());
		}
		else {
			event = new ReactionAdded(postId, eventStore.nextReactionId(), actor, command.type(), LocalDateTime.now());
		}
		eventStore.append(postId, state.version, List.of(event));
		if (event instanceof ReactionAdded added) {
			notificationPublisher.publishReactionAdded(added, state.author);
		}
		ReactionState reaction = stateOf(postId).reactions.get(actor);
		return reaction.toResponse(postId.value(), profileOf(reaction.author));
	}

	@Override
	@Transactional
	public void deleteReaction(DeleteReactionCommand command) {
		PostId postId = new PostId(command.postId());
		PostState state = existingState(postId);
		UserId actor = new UserId(command.actorId());
		ReactionState reaction = state.reactions.get(actor);
		if (reaction != null) {
			eventStore.append(postId, state.version,
					List.of(new ReactionRemoved(postId, reaction.id, actor, LocalDateTime.now())));
		}
	}

	@Override
	public ReactionResponse getMyReaction(Long postId, Actor actor) {
		PostState state = visibleState(new PostId(postId), new UserId(actor.userId()));
		ReactionState reaction = state.reactions.get(new UserId(actor.userId()));
		return reaction == null ? null : reaction.toResponse(postId, profileOf(reaction.author));
	}

	private PostResponse toResponse(PostState state) {
		return state.toResponse(profileOf(state.author));
	}

	private PostService.UserProfileResponse profileOf(UserId userId) {
		PostUserDirectory.UserProfile profile = profilesById(List.of(userId)).get(userId);
		return profile == null ? null
				: new PostService.UserProfileResponse(profile.id().value(), profile.name(), profile.surname(),
						profile.username());
	}

	private Map<UserId, PostUserDirectory.UserProfile> profilesById(List<UserId> userIds) {
		Map<UserId, PostUserDirectory.UserProfile> profiles = userDirectory.findByIds(userIds);
		return profiles == null ? Map.of() : profiles;
	}

	private PostState visibleState(PostId postId, UserId viewer) {
		PostState state = existingState(postId);
		if (canView(state, viewer)) {
			return state;
		}
		throw new PostNotFoundException("Post not found");
	}

	private boolean canView(PostState state, UserId viewer) {
		if (state.author.equals(viewer)) {
			return true;
		}
		PostVisibilityChecker.RelationshipVisibility relationship = visibilityChecker.visibilityBetween(viewer,
				state.author);
		if (relationship.blocked() || !relationship.sameNeighborhood()) {
			return false;
		}
		return state.visibility == PostVisibility.PUBLIC || relationship.activeNeighborship();
	}

	private PostState existingState(PostId postId) {
		PostState state = stateOf(postId);
		if (!state.exists || state.deleted) {
			throw new PostNotFoundException("Post not found");
		}
		return state;
	}

	private PostState stateOf(PostId postId) {
		return PostState.from(eventStore.read(postId));
	}

	private List<PostState> statesFromAllEvents() {
		Map<PostId, List<PostEvent>> byPost = new LinkedHashMap<>();
		for (PostEvent event : eventStore.readAll()) {
			byPost.computeIfAbsent(event.postId(), ignored -> new ArrayList<>()).add(event);
		}
		return byPost.values().stream().map(PostState::from).toList();
	}

	private void requireAuthor(PostState state, UserId actor, String action) {
		requireUser(state.author, actor, action);
	}

	private void requireUser(UserId owner, UserId actor, String action) {
		if (!owner.equals(actor)) {
			throw new PostAuthorizationException("Only the author can " + action);
		}
	}

	private static final class PostState {

		private boolean exists;
		private boolean deleted;
		private long version;
		private PostId id;
		private UserId author;
		private NeighborhoodId neighborhoodId;
		private String content;
		private com.rione.post.domain.model.PostType type;
		private PostVisibility visibility;
		private Place place;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private final Map<CommentId, CommentState> comments = new LinkedHashMap<>();
		private final Map<UserId, ReactionState> reactions = new LinkedHashMap<>();

		static PostState from(List<PostEvent> events) {
			PostState state = new PostState();
			for (PostEvent event : events) {
				state.version++;
				state.apply(event);
			}
			return state;
		}

		private void apply(PostEvent event) {
			if (event instanceof PostCreated created) {
				exists = true;
				id = created.postId();
				author = created.author();
				neighborhoodId = created.neighborhoodId();
				content = created.content().content();
				type = created.postType();
				visibility = created.visibility();
				place = created.place();
				createdAt = created.occurredAt();
				updatedAt = created.occurredAt();
			}
			else if (event instanceof PostContentUpdated updated) {
				content = updated.content().content();
				updatedAt = updated.occurredAt();
			}
			else if (event instanceof PostDeleted deletedEvent) {
				deleted = true;
				updatedAt = deletedEvent.occurredAt();
			}
			else if (event instanceof CommentAdded added) {
				comments.put(added.commentId(), new CommentState(added.commentId(), added.author(),
						added.content().content(), added.occurredAt(), added.occurredAt()));
				updatedAt = added.occurredAt();
			}
			else if (event instanceof CommentUpdated updated) {
				comment(updated.commentId()).update(updated.content().content(), updated.occurredAt());
				updatedAt = updated.occurredAt();
			}
			else if (event instanceof CommentRemoved removed) {
				comments.remove(removed.commentId());
				updatedAt = removed.occurredAt();
			}
			else if (event instanceof ReactionAdded added) {
				reactions.put(added.author(), new ReactionState(added.reactionId(), added.author(),
						added.reactionType(), added.occurredAt(), added.occurredAt()));
				updatedAt = added.occurredAt();
			}
			else if (event instanceof ReactionUpdated updated) {
				reaction(updated.author()).update(updated.reactionType(), updated.occurredAt());
				updatedAt = updated.occurredAt();
			}
			else if (event instanceof ReactionRemoved removed) {
				reactions.remove(removed.author());
				updatedAt = removed.occurredAt();
			}
		}

		private CommentState comment(CommentId commentId) {
			CommentState comment = comments.get(commentId);
			if (comment == null) {
				throw new PostNotFoundException("Comment not found");
			}
			return comment;
		}

		private ReactionState reaction(UserId author) {
			ReactionState reaction = reactions.get(author);
			if (reaction == null) {
				throw new PostNotFoundException("Reaction not found");
			}
			return reaction;
		}

		private PostResponse toResponse(PostService.UserProfileResponse authorResponse) {
			PostService.PlaceResponse placeResponse = place == null ? null
					: new PostService.PlaceResponse(place.longitude(), place.latitude());
			return new PostResponse(id.value(), author.value(), neighborhoodId.value(), content, type.name(),
					visibility.name(), placeResponse, score(), createdAt, updatedAt, authorResponse);
		}

		private int score() {
			return reactions.values().stream().mapToInt(reaction -> reaction.type.scoreValue()).sum();
		}
	}

	private static final class CommentState {

		private final CommentId id;
		private final UserId author;
		private String content;
		private final LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		private CommentState(CommentId id, UserId author, String content, LocalDateTime createdAt,
				LocalDateTime updatedAt) {
			this.id = id;
			this.author = author;
			this.content = content;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
		}

		private void update(String content, LocalDateTime updatedAt) {
			this.content = content;
			this.updatedAt = updatedAt;
		}

		private CommentResponse toResponse(Long postId, PostService.UserProfileResponse authorResponse) {
			return new CommentResponse(id.value(), postId, author.value(), content, createdAt, updatedAt,
					authorResponse);
		}
	}

	private static final class ReactionState {

		private final ReactionId id;
		private final UserId author;
		private ReactionType type;
		private final LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		private ReactionState(ReactionId id, UserId author, ReactionType type, LocalDateTime createdAt,
				LocalDateTime updatedAt) {
			this.id = id;
			this.author = author;
			this.type = type;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
		}

		private void update(ReactionType type, LocalDateTime updatedAt) {
			this.type = Objects.requireNonNull(type);
			this.updatedAt = updatedAt;
		}

		private ReactionResponse toResponse(Long postId, PostService.UserProfileResponse userResponse) {
			return new ReactionResponse(postId, author.value(), type.name(), createdAt, updatedAt, userResponse);
		}
	}
}
