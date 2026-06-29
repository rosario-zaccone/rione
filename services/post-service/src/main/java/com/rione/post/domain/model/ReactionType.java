package com.rione.post.domain.model;

public enum ReactionType {

	UPVOTE,
	DOWNVOTE;

	public int scoreValue() {
		return switch (this) {
			case UPVOTE -> 1;
			case DOWNVOTE -> -1;
		};
	}
}
