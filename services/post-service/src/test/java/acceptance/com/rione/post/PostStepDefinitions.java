package com.rione.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rione.post.application.port.in.PostService;
import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.application.port.out.PostNotificationPublisher;
import com.rione.post.application.port.out.PostUserDirectory;
import com.rione.post.application.port.out.PostVisibilityChecker;
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

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PostStepDefinitions {

	private PostServiceImpl service;
	private FakeEventStore eventStore;
	private FakeVisibilityChecker visibilityChecker;
	private PostService.PostResponse post;
	private PostService.CommentResponse comment;
	private List<PostService.PostResponse> feed;
	private Throwable thrown;

	@Before
	public void setUp() {
		eventStore = new FakeEventStore();
		visibilityChecker = new FakeVisibilityChecker();
		service = new PostServiceImpl(eventStore, new NoopPublisher(), visibilityChecker, new FakeUserDirectory());
		thrown = null;
	}

	@When("user {long} creates a public discussion post")
	public void userCreatesPublicDiscussionPost(Long userId) {
		try {
			post = service.createPost(new PostService.CreatePostCommand(userId, 10L,
					"Neighborhood discussion content", PostType.DISCUSSION, PostVisibility.PUBLIC, null, null));
		}
		catch (Throwable exception) {
			thrown = exception;
		}
	}

	@Given("user {long} created a private help post")
	public void userCreatedPrivateHelpPost(Long userId) {
		post = service.createPost(new PostService.CreatePostCommand(userId, 10L,
				"Private help content for close neighbours", PostType.HELP, PostVisibility.PRIVATE, null, null));
	}

	@Given("user {long} belongs to the same neighborhood as user {long}")
	public void userBelongsToTheSameNeighborhoodAsUser(Long viewerId, Long authorId) {
		visibilityChecker.allowSameNeighborhood(viewerId, authorId);
	}

	@Given("user {long} has active neighborship with user {long}")
	public void userHasActiveNeighborshipWithUser(Long viewerId, Long authorId) {
		visibilityChecker.allowActiveNeighborship(viewerId, authorId);
	}

	@Given("user {long} is blocked with user {long}")
	public void userIsBlockedWithUser(Long viewerId, Long authorId) {
		visibilityChecker.block(viewerId, authorId);
	}

	@When("user {long} views the post")
	public void userViewsThePost(Long userId) {
		try {
			post = service.getPost(post.id(), new PostService.Actor(userId));
		}
		catch (Throwable exception) {
			thrown = exception;
		}
	}

	@When("user {long} views their post feed")
	public void userViewsTheirPostFeed(Long userId) {
		try {
			feed = service.getVisiblePosts(new PostService.Actor(userId));
		}
		catch (Throwable exception) {
			thrown = exception;
		}
	}

	@Then("the post is visible")
	public void thePostIsVisible() {
		assertThat(thrown).isNull();
		assertThat(post).isNotNull();
	}

	@Then("the post feed contains the post")
	public void thePostFeedContainsThePost() {
		assertThat(thrown).isNull();
		assertThat(feed).extracting(PostService.PostResponse::id).contains(post.id());
	}

	@Given("user {long} created a public discussion post")
	public void userCreatedPublicDiscussionPost(Long userId) {
		userCreatesPublicDiscussionPost(userId);
		assertThat(thrown).isNull();
	}

	@Then("the post is created")
	public void thePostIsCreated() {
		assertThat(thrown).isNull();
		assertThat(post.id()).isNotNull();
	}

	@And("user {long} commented on the post")
	public void userCommentedOnThePost(Long userId) {
		comment = service.addComment(new PostService.AddCommentCommand(post.id(), userId,
				"This comment is long enough"));
	}

	@When("user {long} reacts with {string}")
	public void userReactsWith(Long userId, String reactionType) {
		service.upsertReaction(new PostService.UpsertReactionCommand(post.id(), userId, ReactionType.valueOf(reactionType)));
	}

	@Then("the post has one reaction from user {long} with type {string}")
	public void thePostHasOneReaction(Long userId, String reactionType) {
		assertThat(service.getReactions(post.id(), new PostService.Actor(1L)))
			.extracting(PostService.ReactionResponse::userId, PostService.ReactionResponse::type)
			.containsExactly(org.assertj.core.groups.Tuple.tuple(userId, reactionType));
	}

	@When("user {long} updates the comment")
	public void userUpdatesTheComment(Long userId) {
		try {
			service.updateComment(new PostService.UpdateCommentCommand(post.id(), comment.id(), userId,
					"Updated comment is long enough"));
		}
		catch (Throwable exception) {
			thrown = exception;
		}
	}

	@Then("the post operation is rejected with {string}")
	public void operationRejectedWith(String message) {
		assertThat(thrown).hasMessage(message);
	}

	private static final class NoopPublisher implements PostNotificationPublisher {

		@Override
		public void publishCommentAdded(CommentAdded event, UserId postAuthor) {
		}

		@Override
		public void publishReactionAdded(ReactionAdded event, UserId postAuthor) {
		}
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
			events.computeIfAbsent(postId, ignored -> new ArrayList<>()).addAll(newEvents);
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
