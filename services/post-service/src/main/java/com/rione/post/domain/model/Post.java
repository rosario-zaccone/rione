package com.rione.post.domain.model;

import com.rione.common.domain.DDDAggregateRoot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@DDDAggregateRoot
public class Post {

	private final PostId id;
	private final UserId author;
	private final NeighborhoodId neighborhoodId;
	private final LocalDateTime date;
	private final PostType type;
	private final PostVisibility visibility;
	private final Place place;
	private final List<Comment> comments;
	private final List<Reaction> reactions;
	private PostContent content;
	private int score;

	private Post(PostId id, UserId author, NeighborhoodId neighborhoodId, PostContent content, Place place,
			LocalDateTime date, PostType type, PostVisibility visibility, Collection<Comment> comments,
			Collection<Reaction> reactions) {
		this.id = id;
		this.author = require(author, "Post author");
		this.neighborhoodId = require(neighborhoodId, "Post neighborhood");
		this.content = require(content, "Post content");
		this.place = place;
		this.date = date == null ? LocalDateTime.now() : date;
		this.type = require(type, "Post type");
		this.visibility = require(visibility, "Post visibility");
		this.comments = copyComments(comments);
		this.reactions = copyReactions(reactions);
		ensureUniqueCommentIds();
		ensureOneReactionPerUser();
		recalculateScore();
	}

	public static Post create(UserId author, NeighborhoodId neighborhoodId, PostContent content, Place place,
			PostType type, PostVisibility visibility, LocalDateTime date) {
		return new Post(null, author, neighborhoodId, content, place, date, type, visibility, List.of(), List.of());
	}

	public static Post restore(PostId id, UserId author, NeighborhoodId neighborhoodId, PostContent content,
			Place place, LocalDateTime date, PostType type, PostVisibility visibility, Collection<Comment> comments,
			Collection<Reaction> reactions) {
		return new Post(id, author, neighborhoodId, content, place, date, type, visibility, comments, reactions);
	}

	public void update(PostContent content) {
		this.content = require(content, "Post content");
	}

	public Comment addComment(UserId author, CommentContent content) {
		Comment comment = Comment.create(author, content);
		comments.add(comment);
		return comment;
	}

	public void updateComment(CommentId commentId, UserId actingUser, CommentContent content) {
		Comment comment = commentById(commentId);
		comment.requireAuthor(require(actingUser, "Acting user"), "update this comment");
		comment.update(content);
	}

	public void removeComment(CommentId commentId, UserId actingUser) {
		Comment comment = commentById(commentId);
		comment.requireAuthor(require(actingUser, "Acting user"), "remove this comment");
		comments.remove(comment);
	}

	public Reaction react(UserId author, ReactionType type) {
		UserId reactionAuthor = require(author, "Reaction author");
		if (reactionByAuthor(reactionAuthor).isPresent()) {
			throw new DomainException("User already reacted to this post");
		}
		Reaction reaction = Reaction.create(reactionAuthor, type);
		reactions.add(reaction);
		recalculateScore();
		return reaction;
	}

	public void updateReaction(UserId author, ReactionType type) {
		Reaction reaction = reactionByAuthor(require(author, "Reaction author"))
			.orElseThrow(() -> new DomainException("User has not reacted to this post"));
		reaction.update(type);
		recalculateScore();
	}

	public void removeReaction(UserId author) {
		UserId reactionAuthor = require(author, "Reaction author");
		boolean removed = reactions.removeIf(reaction -> reaction.isAuthoredBy(reactionAuthor));
		if (removed) {
			recalculateScore();
		}
	}

	public boolean isVisibleTo(UserId viewer, NeighborhoodId viewerNeighborhoodId, boolean activeNeighborshipWithAuthor,
			boolean blocked) {
		UserId candidate = require(viewer, "Viewer");
		if (isAuthoredBy(candidate)) {
			return true;
		}
		if (blocked) {
			return false;
		}
		if (!neighborhoodId.equals(require(viewerNeighborhoodId, "Viewer neighborhood"))) {
			return false;
		}
		return visibility.isPublic() || activeNeighborshipWithAuthor;
	}

	public boolean isAuthoredBy(UserId userId) {
		return author.equals(userId);
	}

	public Optional<Comment> findComment(CommentId commentId) {
		CommentId id = require(commentId, "Comment id");
		return comments.stream().filter(comment -> id.equals(comment.id())).findFirst();
	}

	public Optional<Reaction> reactionByAuthor(UserId author) {
		UserId reactionAuthor = require(author, "Reaction author");
		return reactions.stream().filter(reaction -> reaction.isAuthoredBy(reactionAuthor)).findFirst();
	}

	public PostId id() {
		return id;
	}

	public UserId author() {
		return author;
	}

	public NeighborhoodId neighborhoodId() {
		return neighborhoodId;
	}

	public PostContent content() {
		return content;
	}

	public Place place() {
		return place;
	}

	public LocalDateTime date() {
		return date;
	}

	public PostType type() {
		return type;
	}

	public PostVisibility visibility() {
		return visibility;
	}

	public boolean isPrivate() {
		return visibility.isPrivate();
	}

	public int score() {
		return score;
	}

	public List<Comment> comments() {
		return List.copyOf(comments);
	}

	public List<Reaction> reactions() {
		return List.copyOf(reactions);
	}

	private Comment commentById(CommentId commentId) {
		return findComment(commentId).orElseThrow(() -> new DomainException("Comment does not belong to this post"));
	}

	private void recalculateScore() {
		score = reactions.stream().mapToInt(reaction -> reaction.type().scoreValue()).sum();
	}

	private void ensureUniqueCommentIds() {
		Set<CommentId> ids = new HashSet<>();
		for (Comment comment : comments) {
			CommentId commentId = comment.id();
			if (commentId != null && !ids.add(commentId)) {
				throw new DomainException("Post cannot contain duplicate comments");
			}
		}
	}

	private void ensureOneReactionPerUser() {
		Set<UserId> authors = new HashSet<>();
		for (Reaction reaction : reactions) {
			if (!authors.add(reaction.author())) {
				throw new DomainException("Post cannot contain multiple reactions by the same user");
			}
		}
	}

	private static List<Comment> copyComments(Collection<Comment> comments) {
		if (comments == null) {
			return new ArrayList<>();
		}
		List<Comment> copied = new ArrayList<>(comments.size());
		for (Comment comment : comments) {
			copied.add(require(comment, "Comment"));
		}
		return copied;
	}

	private static List<Reaction> copyReactions(Collection<Reaction> reactions) {
		if (reactions == null) {
			return new ArrayList<>();
		}
		List<Reaction> copied = new ArrayList<>(reactions.size());
		for (Reaction reaction : reactions) {
			copied.add(require(reaction, "Reaction"));
		}
		return copied;
	}

	private static <T> T require(T value, String fieldName) {
		if (value == null) {
			throw new DomainException(fieldName + " is required");
		}
		return value;
	}
}
