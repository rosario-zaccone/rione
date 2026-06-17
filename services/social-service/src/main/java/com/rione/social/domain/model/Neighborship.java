package com.rione.social.domain.model;

import com.rione.common.domain.DDDAggregateRoot;

import java.time.LocalDateTime;

@DDDAggregateRoot
public class Neighborship {

	private final NeighborshipId id;
	private final UserId follower;
	private final UserId followed;
	private final LocalDateTime date;

	private Neighborship(NeighborshipId id, UserId follower, UserId followed, LocalDateTime date) {
		if (follower.equals(followed)) {
			throw new DomainException("Neighborship users must be different");
		}
		this.id = id;
		this.follower = follower;
		this.followed = followed;
		this.date = date == null ? LocalDateTime.now() : date;
	}

	public static Neighborship create(NeighborshipId id, UserId follower, UserId followed, LocalDateTime date) {
		return new Neighborship(id, follower, followed, date);
	}

	public NeighborshipId id() {
		return id;
	}

	public UserId follower() {
		return follower;
	}

	public UserId followed() {
		return followed;
	}

	public LocalDateTime date() {
		return date;
	}
}
