package com.rione.post.domain.model;

public enum PostVisibility {

	PUBLIC,
	PRIVATE;

	public boolean isPublic() {
		return this == PUBLIC;
	}

	public boolean isPrivate() {
		return this == PRIVATE;
	}
}
