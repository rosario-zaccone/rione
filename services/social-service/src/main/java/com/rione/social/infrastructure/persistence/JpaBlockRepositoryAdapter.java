package com.rione.social.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.rione.social.application.port.out.BlockRepository;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.BlockId;
import com.rione.social.domain.model.UserId;
import com.rione.social.infrastructure.persistence.entity.BlockJpaEntity;

@Repository
class JpaBlockRepositoryAdapter implements BlockRepository {

	private final SpringDataBlockJpaRepository repository;

	JpaBlockRepositoryAdapter(SpringDataBlockJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public Block save(Block block) {
		return toDomain(repository.save(toEntity(block)));
	}

	@Override
	public List<Block> findByBlocker(UserId blocker) {
		return repository.findByBlockerId(blocker.value()).stream().map(this::toDomain).toList();
	}

	@Override
	public Optional<Block> findBetween(UserId blocker, UserId blocked) {
		return repository.findByBlockerIdAndBlockedId(blocker.value(), blocked.value()).map(this::toDomain);
	}

	@Override
	public boolean existsBetween(UserId blocker, UserId blocked) {
		return repository.existsByBlockerIdAndBlockedId(blocker.value(), blocked.value());
	}

	@Override
	@Transactional
	public void deleteBetween(UserId blocker, UserId blocked) {
		repository.deleteByBlockerIdAndBlockedId(blocker.value(), blocked.value());
	}

	private BlockJpaEntity toEntity(Block block) {
		Long id = block.id() == null ? null : block.id().value();
		return new BlockJpaEntity(id, block.blocker().value(), block.blocked().value());
	}

	private Block toDomain(BlockJpaEntity entity) {
		return Block.restore(new BlockId(entity.id()), new UserId(entity.blockerId()), new UserId(entity.blockedId()));
	}
}
