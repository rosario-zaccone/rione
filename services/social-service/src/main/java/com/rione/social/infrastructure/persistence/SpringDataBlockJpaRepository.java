package com.rione.social.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rione.social.infrastructure.persistence.entity.BlockJpaEntity;

interface SpringDataBlockJpaRepository extends JpaRepository<BlockJpaEntity, Long> {

	List<BlockJpaEntity> findByBlockerId(Long blockerId);

	Optional<BlockJpaEntity> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

	boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

	void deleteByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}
