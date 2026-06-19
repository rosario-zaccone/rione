package com.rione.social.domain.model;

import com.rione.common.domain.DDDAggregateRoot;

@DDDAggregateRoot
public class Block {

	private final BlockId id;
	private final UserId blocker;
	private final UserId blocked;

	private Block(BlockId id, UserId blocker, UserId blocked) {
		if (blocker.equals(blocked)) {
			throw new DomainException("Blocker and blocked user must be different users");
		}
		this.id = id;
		this.blocker = blocker;
		this.blocked = blocked;
	}

	public static Block create(UserId blocker, UserId blocked) {
		return new Block(null, blocker, blocked);
	}

	public static Block restore(BlockId id, UserId blocker, UserId blocked) {
		return new Block(id, blocker, blocked);
	}

	public BlockId id() {
		return id;
	}

	public UserId blocker() {
		return blocker;
	}

	public UserId blocked() {
		return blocked;
	}
}
