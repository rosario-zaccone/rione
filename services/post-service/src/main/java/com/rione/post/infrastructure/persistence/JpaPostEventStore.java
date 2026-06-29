package com.rione.post.infrastructure.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.application.service.PostApplicationException;
import com.rione.post.domain.event.CommentAdded;
import com.rione.post.domain.event.CommentRemoved;
import com.rione.post.domain.event.CommentUpdated;
import com.rione.post.domain.event.PostContentUpdated;
import com.rione.post.domain.event.PostCreated;
import com.rione.post.domain.event.PostDeleted;
import com.rione.post.domain.event.PostEvent;
import com.rione.post.domain.event.PostEventType;
import com.rione.post.domain.event.ReactionAdded;
import com.rione.post.domain.event.ReactionRemoved;
import com.rione.post.domain.event.ReactionUpdated;
import com.rione.post.domain.model.CommentContent;
import com.rione.post.domain.model.CommentId;
import com.rione.post.domain.model.NeighborhoodId;
import com.rione.post.domain.model.Place;
import com.rione.post.domain.model.PostContent;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.PostType;
import com.rione.post.domain.model.PostVisibility;
import com.rione.post.domain.model.ReactionId;
import com.rione.post.domain.model.ReactionType;
import com.rione.post.domain.model.UserId;

import tools.jackson.databind.ObjectMapper;

@Repository
class JpaPostEventStore implements PostEventStore {

	private final SpringDataPostEventJpaRepository events;
	private final SpringDataPostIdJpaRepository postIds;
	private final SpringDataCommentIdJpaRepository commentIds;
	private final SpringDataReactionIdJpaRepository reactionIds;
	private final ObjectMapper objectMapper;

	JpaPostEventStore(SpringDataPostEventJpaRepository events, SpringDataPostIdJpaRepository postIds,
			SpringDataCommentIdJpaRepository commentIds, SpringDataReactionIdJpaRepository reactionIds,
			ObjectMapper objectMapper) {
		this.events = events;
		this.postIds = postIds;
		this.commentIds = commentIds;
		this.reactionIds = reactionIds;
		this.objectMapper = objectMapper;
	}

	@Override
	public PostId nextPostId() {
		return new PostId(postIds.save(new PostIdJpaEntity()).id());
	}

	@Override
	public CommentId nextCommentId() {
		return new CommentId(commentIds.save(new CommentIdJpaEntity()).id());
	}

	@Override
	public ReactionId nextReactionId() {
		return new ReactionId(reactionIds.save(new ReactionIdJpaEntity()).id());
	}

	@Override
	public List<PostEvent> read(PostId postId) {
		return events.findByPostIdOrderByVersionAsc(postId.value()).stream().map(this::toDomain).toList();
	}

	@Override
	public List<PostEvent> readAll() {
		return events.findAllByOrderByPostIdAscVersionAsc().stream().map(this::toDomain).toList();
	}

	@Override
	public long countEvents(PostEventType type) {
		return events.countByType(type);
	}

	@Override
	public long countEventsSince(PostEventType type, LocalDate date) {
		return events.countByTypeAndOccurredAtGreaterThanEqual(type, date.atStartOfDay());
	}

	@Override
	public void append(PostId postId, long expectedVersion, List<PostEvent> newEvents) {
		long actualVersion = events.countByPostId(postId.value());
		if (actualVersion != expectedVersion) {
			throw new PostApplicationException("Post was modified concurrently");
		}
		try {
			long version = expectedVersion;
			for (PostEvent event : newEvents) {
				events.save(new PostEventJpaEntity(postId.value(), ++version, event.type(), event.occurredAt(),
						writePayload(event)));
			}
		}
		catch (DataIntegrityViolationException exception) {
			throw new PostApplicationException("Post was modified concurrently");
		}
	}

	private String writePayload(PostEvent event) {
		try {
			return new String(objectMapper.writeValueAsBytes(toPayload(event)), java.nio.charset.StandardCharsets.UTF_8);
		}
		catch (Exception exception) {
			throw new IllegalStateException("Unable to serialize post event", exception);
		}
	}

	private PostEvent toDomain(PostEventJpaEntity entity) {
		try {
			byte[] bytes = entity.payload().getBytes(java.nio.charset.StandardCharsets.UTF_8);
			return switch (entity.type()) {
				case POST_CREATED -> {
					PostCreatedPayload payload = objectMapper.readValue(bytes, PostCreatedPayload.class);
					Place place = payload.longitude() == null || payload.latitude() == null ? null
							: new Place(payload.longitude(), payload.latitude());
					yield new PostCreated(new PostId(entity.postId()), new UserId(payload.authorId()),
							new NeighborhoodId(payload.neighborhoodId()), new PostContent(payload.content()), place,
							payload.postType(), payload.visibility(), entity.occurredAt());
				}
				case POST_CONTENT_UPDATED -> {
					PostContentUpdatedPayload payload = objectMapper.readValue(bytes, PostContentUpdatedPayload.class);
					yield new PostContentUpdated(new PostId(entity.postId()), new UserId(payload.authorId()),
							new PostContent(payload.content()), entity.occurredAt());
				}
				case POST_DELETED -> {
					PostDeletedPayload payload = objectMapper.readValue(bytes, PostDeletedPayload.class);
					yield new PostDeleted(new PostId(entity.postId()), new UserId(payload.authorId()), entity.occurredAt());
				}
				case COMMENT_ADDED -> {
					CommentPayload payload = objectMapper.readValue(bytes, CommentPayload.class);
					yield new CommentAdded(new PostId(entity.postId()), new CommentId(payload.commentId()),
							new UserId(payload.authorId()), new CommentContent(payload.content()), entity.occurredAt());
				}
				case COMMENT_UPDATED -> {
					CommentPayload payload = objectMapper.readValue(bytes, CommentPayload.class);
					yield new CommentUpdated(new PostId(entity.postId()), new CommentId(payload.commentId()),
							new UserId(payload.authorId()), new CommentContent(payload.content()), entity.occurredAt());
				}
				case COMMENT_REMOVED -> {
					CommentRemovedPayload payload = objectMapper.readValue(bytes, CommentRemovedPayload.class);
					yield new CommentRemoved(new PostId(entity.postId()), new CommentId(payload.commentId()),
							new UserId(payload.authorId()), entity.occurredAt());
				}
				case REACTION_ADDED -> {
					ReactionPayload payload = objectMapper.readValue(bytes, ReactionPayload.class);
					yield new ReactionAdded(new PostId(entity.postId()), new ReactionId(payload.reactionId()),
							new UserId(payload.authorId()), payload.reactionType(), entity.occurredAt());
				}
				case REACTION_UPDATED -> {
					ReactionPayload payload = objectMapper.readValue(bytes, ReactionPayload.class);
					yield new ReactionUpdated(new PostId(entity.postId()), new ReactionId(payload.reactionId()),
							new UserId(payload.authorId()), payload.reactionType(), entity.occurredAt());
				}
				case REACTION_REMOVED -> {
					ReactionRemovedPayload payload = objectMapper.readValue(bytes, ReactionRemovedPayload.class);
					yield new ReactionRemoved(new PostId(entity.postId()), new ReactionId(payload.reactionId()),
							new UserId(payload.authorId()), entity.occurredAt());
				}
			};
		}
		catch (Exception exception) {
			throw new IllegalStateException("Unable to deserialize post event", exception);
		}
	}

	private Object toPayload(PostEvent event) {
		return switch (event) {
			case PostCreated created -> new PostCreatedPayload(created.author().value(), created.neighborhoodId().value(),
					created.content().content(), created.place() == null ? null : created.place().longitude(),
					created.place() == null ? null : created.place().latitude(), created.postType(),
					created.visibility());
			case PostContentUpdated updated -> new PostContentUpdatedPayload(updated.author().value(),
					updated.content().content());
			case PostDeleted deleted -> new PostDeletedPayload(deleted.author().value());
			case CommentAdded added -> new CommentPayload(added.commentId().value(), added.author().value(),
					added.content().content());
			case CommentUpdated updated -> new CommentPayload(updated.commentId().value(), updated.author().value(),
					updated.content().content());
			case CommentRemoved removed -> new CommentRemovedPayload(removed.commentId().value(), removed.author().value());
			case ReactionAdded added -> new ReactionPayload(added.reactionId().value(), added.author().value(),
					added.reactionType());
			case ReactionUpdated updated -> new ReactionPayload(updated.reactionId().value(), updated.author().value(),
					updated.reactionType());
			case ReactionRemoved removed -> new ReactionRemovedPayload(removed.reactionId().value(),
					removed.author().value());
		};
	}

	record PostCreatedPayload(Long authorId, Long neighborhoodId, String content, Double longitude, Double latitude,
			PostType postType, PostVisibility visibility) {
	}

	record PostContentUpdatedPayload(Long authorId, String content) {
	}

	record PostDeletedPayload(Long authorId) {
	}

	record CommentPayload(Long commentId, Long authorId, String content) {
	}

	record CommentRemovedPayload(Long commentId, Long authorId) {
	}

	record ReactionPayload(Long reactionId, Long authorId, ReactionType reactionType) {
	}

	record ReactionRemovedPayload(Long reactionId, Long authorId) {
	}
}
