package com.rione.social.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rione.social.infrastructure.persistence.entity.BlockJpaEntity;

interface SpringDataBlockJpaRepository extends JpaRepository<BlockJpaEntity, Long> {

	boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

	void deleteByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}
