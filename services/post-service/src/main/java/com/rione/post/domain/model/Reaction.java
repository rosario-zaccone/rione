package com.rione.post.domain.model;

import com.rione.common.domain.DDDEntity;

@DDDEntity
public class Reaction {

	private final ReactionId id;
	private final UserId author;
	private ReactionType type;

	private Reaction(ReactionId id, UserId author, ReactionType type) {
		this.id = id;
		this.author = require(author, "Reaction author");
		this.type = require(type, "Reaction type");
	}

	public static Reaction create(UserId author, ReactionType type) {
		return new Reaction(null, author, type);
	}

	public static Reaction restore(ReactionId id, UserId author, ReactionType type) {
		return new Reaction(id, author, type);
	}

	void update(ReactionType type) {
		this.type = require(type, "Reaction type");
	}

	public boolean isAuthoredBy(UserId userId) {
		return author.equals(userId);
	}

	public ReactionId id() {
		return id;
	}

	public UserId author() {
		return author;
	}

	public ReactionType type() {
		return type;
	}

	private static <T> T require(T value, String fieldName) {
		if (value == null) {
			throw new DomainException(fieldName + " is required");
		}
		return value;
	}
}
