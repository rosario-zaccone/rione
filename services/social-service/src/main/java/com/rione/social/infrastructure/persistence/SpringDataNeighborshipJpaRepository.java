package com.rione.social.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rione.social.infrastructure.persistence.entity.NeighborshipJpaEntity;

interface SpringDataNeighborshipJpaRepository extends JpaRepository<NeighborshipJpaEntity, Long> {

	List<NeighborshipJpaEntity> findByFollowerIdOrFollowedId(Long followerId, Long followedId);

	boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

	void deleteByFollowerIdAndFollowedId(Long followerId, Long followedId);
}
