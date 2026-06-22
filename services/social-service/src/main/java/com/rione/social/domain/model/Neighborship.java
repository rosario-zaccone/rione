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
		validateUsers(follower, followed);
		this.id = id;
		this.follower = follower;
		this.followed = followed;
		this.date = date == null ? LocalDateTime.now() : date;
	}

	public static void validateUsers(UserId firstUser, UserId secondUser) {
		if (firstUser.equals(secondUser)) {
			throw new DomainException("Neighborship users must be different");
		}
	}

	public static Neighborship create(UserId follower, UserId followed, LocalDateTime date) {
		return new Neighborship(null, follower, followed, date);
	}

	public static Neighborship restore(NeighborshipId id, UserId follower, UserId followed, LocalDateTime date) {
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

	public boolean involves(UserId userId) {
		return follower.equals(userId) || followed.equals(userId);
	}

	public UserId counterpartOf(UserId userId) {
		if (follower.equals(userId)) {
			return followed;
		}
		if (followed.equals(userId)) {
			return follower;
		}
		throw new DomainException("User is not part of this neighborship");
	}
}
