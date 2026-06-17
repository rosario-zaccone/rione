package com.rione.social.infrastructure.persistence;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.BlockId;
import com.rione.social.domain.model.UserId;

@Repository
class InMemoryBlockRepository implements BlockRepository {

	private final AtomicLong sequence = new AtomicLong();
	private final Map<BlockId, Block> blocks = new ConcurrentHashMap<>();

	@Override
	public BlockId nextIdentity() {
		return new BlockId(sequence.incrementAndGet());
	}

	@Override
	public Block save(Block block) {
		blocks.put(block.id(), block);
		return block;
	}

	@Override
	public boolean existsBetween(UserId blocker, UserId blocked) {
		return blocks.values()
			.stream()
			.anyMatch(block -> block.blocker().equals(blocker) && block.blocked().equals(blocked));
	}
}
