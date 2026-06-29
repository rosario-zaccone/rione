package com.rione.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.rione.post.application.port.in.PostService;
import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.application.port.out.PostNotificationPublisher;
import com.rione.post.application.port.out.PostUserDirectory;
import com.rione.post.application.port.out.PostVisibilityChecker;
import com.rione.post.application.service.PostAuthorizationException;
import com.rione.post.application.service.PostNotFoundException;
import com.rione.post.application.service.PostServiceImpl;
import com.rione.post.domain.event.CommentAdded;
import com.rione.post.domain.event.PostEvent;
import com.rione.post.domain.event.PostEventType;
import com.rione.post.domain.event.ReactionAdded;
import com.rione.post.domain.model.CommentId;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.PostType;
import com.rione.post.domain.model.PostVisibility;
import com.rione.post.domain.model.ReactionId;
import com.rione.post.domain.model.ReactionType;
import com.rione.post.domain.model.UserId;

class PostServiceImplTest {

	private FakeEventStore eventStore;
	private RecordingPublisher publisher;
	private FakeVisibilityChecker visibilityChecker;
	private PostServiceImpl service;

	@BeforeEach
	void setUp() {
		eventStore = new FakeEventStore();
		publisher = new RecordingPublisher();
		visibilityChecker = new FakeVisibilityChecker();
		service = new PostServiceImpl(eventStore, publisher, visibilityChecker, new FakeUserDirectory());
	}

	@Test
	void createsAndListsOwnPostsFromEventStream() {
		PostService.PostResponse created = service.createPost(new PostService.CreatePostCommand(1L, 10L,
				"Neighborhood discussion content", PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));

		assertThat(created.id()).isEqualTo(1L);
		assertThat(service.getMyPosts(1L)).extracting(PostService.PostResponse::content)
			.containsExactly("Neighborhood discussion content");
	}

	@Test
	void rejectsUpdatingPostByAnotherUser() {
		PostService.PostResponse post = service.createPost(new PostService.CreatePostCommand(1L, 10L,
				"Neighborhood discussion content", PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));

		assertThatThrownBy(
				() -> service.updatePost(new PostService.UpdatePostCommand(post.id(), 2L, "Updated content long enough")))
			.isInstanceOf(PostAuthorizationException.class)
			.hasMessage("Only the author can update this post");
	}

	@Test
	void addingCommentPublishesNotificationForPostAuthor() {
		PostService.PostResponse post = service.createPost(new PostService.CreatePostCommand(1L, 10L,
				"Neighborhood discussion content", PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));
		visibilityChecker.allowSameNeighborhood(2L, 1L);

		service.addComment(new PostService.AddCommentCommand(post.id(), 2L, "This comment is long enough"));

		assertThat(publisher.commentEvents).hasSize(1);
		assertThat(publisher.commentRecipients).extracting(UserId::value).containsExactly(1L);
	}

	@Test
	void upsertsSingleReactionPerUser() {
		PostService.PostResponse post = service.createPost(new PostService.CreatePostCommand(1L, 10L,
				"Neighborhood discussion content", PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));
		visibilityChecker.allowSameNeighborhood(2L, 1L);

		service.upsertReaction(new PostService.UpsertReactionCommand(post.id(), 2L, ReactionType.UPVOTE));
		service.upsertReaction(new PostService.UpsertReactionCommand(post.id(), 2L, ReactionType.DOWNVOTE));

		assertThat(service.getReactions(post.id(), new PostService.Actor(1L))).extracting(PostService.ReactionResponse::type)
			.containsExactly("DOWNVOTE");
		assertThat(publisher.reactionEvents).hasSize(1);
	}

	@Test
	void calculatesPostScoreFromUpvotesAndDownvotes() {
		PostService.PostResponse post = service.createPost(new PostService.CreatePostCommand(1L, 10L,
				"Neighborhood discussion content", PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));
		visibilityChecker.allowSameNeighborhood(2L, 1L);
		visibilityChecker.allowSameNeighborhood(3L, 1L);

		service.upsertReaction(new PostService.UpsertReactionCommand(post.id(), 2L, ReactionType.UPVOTE));
		service.upsertReaction(new PostService.UpsertReactionCommand(post.id(), 3L, ReactionType.DOWNVOTE));

		assertThat(service.getPost(post.id(), new PostService.Actor(1L)).score()).isZero();
	}

	@Test
	void publicPostsAreVisibleToSameNeighborhoodUsersWithoutNeighborship() {
		PostService.PostResponse post = service.createPost(new PostService.CreatePostCommand(1L, 10L,
				"Neighborhood discussion content", PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));
		visibilityChecker.allowSameNeighborhood(2L, 1L);

		assertThat(service.getPost(post.id(), new PostService.Actor(2L)).id()).isEqualTo(post.id());
	}

	@Test
	void privatePostsRequireSameNeighborhoodAndActiveNeighborship() {
		PostService.PostResponse post = service.createPost(new PostService.CreatePostCommand(1L, 10L,
				"Private help content for close neighbours", PostType.HELP, PostVisibility.PRIVATE, null, null));
		visibilityChecker.allowSameNeighborhood(2L, 1L);

		assertThatThrownBy(() -> service.getPost(post.id(), new PostService.Actor(2L)))
			.isInstanceOf(PostNotFoundException.class)
			.hasMessage("Post not found");

		visibilityChecker.allowActiveNeighborship(2L, 1L);

		assertThat(service.getPost(post.id(), new PostService.Actor(2L)).id()).isEqualTo(post.id());
	}

	@Test
	void blockedUsersCannotSeePublicPosts() {
		PostService.PostResponse post = service.createPost(new PostService.CreatePostCommand(1L, 10L,
				"Neighborhood discussion content", PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));
		visibilityChecker.block(2L, 1L);

		assertThatThrownBy(() -> service.getPost(post.id(), new PostService.Actor(2L)))
			.isInstanceOf(PostNotFoundException.class)
			.hasMessage("Post not found");
	}

	@Test
	void listsSpecificAuthorPostsUsingVisibilityRules() {
		service.createPost(new PostService.CreatePostCommand(1L, 10L, "Public discussion content",
				PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));
		service.createPost(new PostService.CreatePostCommand(1L, 10L, "Private help content here", PostType.HELP,
				PostVisibility.PRIVATE, null, null));
		visibilityChecker.allowSameNeighborhood(2L, 1L);

		assertThat(service.getPostsByAuthor(1L, new PostService.Actor(2L)))
			.extracting(PostService.PostResponse::content)
			.containsExactly("Public discussion content");

		visibilityChecker.allowActiveNeighborship(2L, 1L);

		assertThat(service.getPostsByAuthor(1L, new PostService.Actor(2L)))
			.extracting(PostService.PostResponse::content)
			.containsExactly("Public discussion content", "Private help content here");
	}

	@Test
	void publicAuthorProfilePostsExcludePrivatePostsEvenForNeighbors() {
		service.createPost(new PostService.CreatePostCommand(1L, 10L, "Public discussion content",
				PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));
		service.createPost(new PostService.CreatePostCommand(1L, 10L, "Private help content here", PostType.HELP,
				PostVisibility.PRIVATE, null, null));
		visibilityChecker.allowActiveNeighborship(2L, 1L);

		assertThat(service.getPublicPostsByAuthor(1L)).extracting(PostService.PostResponse::content)
			.containsExactly("Public discussion content");
	}

	@Test
	void listsVisibleFeedPostsUsingVisibilityRules() {
		service.createPost(new PostService.CreatePostCommand(1L, 10L, "Visible public discussion",
				PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));
		service.createPost(new PostService.CreatePostCommand(1L, 10L, "Hidden private help content",
				PostType.HELP,
				PostVisibility.PRIVATE, null, null));
		service.createPost(new PostService.CreatePostCommand(3L, 10L, "Viewer own post content",
				PostType.DISCUSSION, PostVisibility.PRIVATE, null, null));
		visibilityChecker.allowSameNeighborhood(3L, 1L);

		assertThat(service.getVisiblePosts(new PostService.Actor(3L))).extracting(PostService.PostResponse::content)
			.containsExactly("Viewer own post content", "Visible public discussion");
	}

	private static final class FakeEventStore implements PostEventStore {

		private long postSequence;
		private long commentSequence;
		private long reactionSequence;
		private final Map<PostId, List<PostEvent>> events = new LinkedHashMap<>();

		@Override
		public PostId nextPostId() {
			return new PostId(++postSequence);
		}

		@Override
		public CommentId nextCommentId() {
			return new CommentId(++commentSequence);
		}

		@Override
		public ReactionId nextReactionId() {
			return new ReactionId(++reactionSequence);
		}

		@Override
		public List<PostEvent> read(PostId postId) {
			return List.copyOf(events.getOrDefault(postId, List.of()));
		}

		@Override
		public List<PostEvent> readAll() {
			return events.values().stream().flatMap(List::stream).toList();
		}

		@Override
		public long countEvents(PostEventType type) {
			return readAll().stream().filter(event -> event.type() == type).count();
		}

		@Override
		public long countEventsSince(PostEventType type, LocalDate date) {
			return readAll().stream()
				.filter(event -> event.type() == type)
				.filter(event -> !event.occurredAt().isBefore(date.atStartOfDay()))
				.count();
		}

		@Override
		public void append(PostId postId, long expectedVersion, List<PostEvent> newEvents) {
			List<PostEvent> stream = events.computeIfAbsent(postId, ignored -> new ArrayList<>());
			assertThat(stream).hasSize((int) expectedVersion);
			stream.addAll(newEvents);
		}
	}

	private static final class RecordingPublisher implements PostNotificationPublisher {

		private final List<CommentAdded> commentEvents = new ArrayList<>();
		private final List<UserId> commentRecipients = new ArrayList<>();
		private final List<ReactionAdded> reactionEvents = new ArrayList<>();

		@Override
		public void publishCommentAdded(CommentAdded event, UserId postAuthor) {
			commentEvents.add(event);
			commentRecipients.add(postAuthor);
		}

		@Override
		public void publishReactionAdded(ReactionAdded event, UserId postAuthor) {
			reactionEvents.add(event);
		}
	}

	private static final class FakeVisibilityChecker implements PostVisibilityChecker {

		private final Map<String, RelationshipVisibility> relationships = new LinkedHashMap<>();

		void allowSameNeighborhood(Long viewerId, Long authorId) {
			relationships.put(key(viewerId, authorId), new RelationshipVisibility(true, false, false));
		}

		void allowActiveNeighborship(Long viewerId, Long authorId) {
			relationships.put(key(viewerId, authorId), new RelationshipVisibility(true, true, false));
		}

		void block(Long viewerId, Long authorId) {
			relationships.put(key(viewerId, authorId), new RelationshipVisibility(false, false, true));
		}

		@Override
		public RelationshipVisibility visibilityBetween(UserId viewer, UserId author) {
			return relationships.getOrDefault(key(viewer.value(), author.value()),
					new RelationshipVisibility(false, false, false));
		}

		private static String key(Long viewerId, Long authorId) {
			return viewerId + ":" + authorId;
		}
	}

	private static final class FakeUserDirectory implements PostUserDirectory {

		@Override
		public Map<UserId, UserProfile> findByIds(java.util.Collection<UserId> userIds) {
			Map<UserId, UserProfile> profiles = new LinkedHashMap<>();
			for (UserId userId : userIds) {
				profiles.put(userId, new UserProfile(userId, "User", userId.value().toString(),
						"user" + userId.value()));
			}
			return profiles;
		}
	}
}
