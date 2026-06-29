package com.rione.post.domain.model;

import com.rione.common.domain.DDDEntity;

@DDDEntity
public class Comment {

	private final CommentId id;
	private final UserId author;
	private CommentContent content;

	private Comment(CommentId id, UserId author, CommentContent content) {
		this.id = id;
		this.author = require(author, "Comment author");
		this.content = require(content, "Comment content");
	}

	public static Comment create(UserId author, CommentContent content) {
		return new Comment(null, author, content);
	}

	public static Comment restore(CommentId id, UserId author, CommentContent content) {
		return new Comment(id, author, content);
	}

	void update(CommentContent content) {
		this.content = require(content, "Comment content");
	}

	void requireAuthor(UserId actingUser, String action) {
		if (!isAuthoredBy(actingUser)) {
			throw new DomainException("Only the comment author can " + action);
		}
	}

	public boolean isAuthoredBy(UserId userId) {
		return author.equals(userId);
	}

	public CommentId id() {
		return id;
	}

	public UserId author() {
		return author;
	}

	public CommentContent content() {
		return content;
	}

	private static <T> T require(T value, String fieldName) {
		if (value == null) {
			throw new DomainException(fieldName + " is required");
		}
		return value;
	}
}
