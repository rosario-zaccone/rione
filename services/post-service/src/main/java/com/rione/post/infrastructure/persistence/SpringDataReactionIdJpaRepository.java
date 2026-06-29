package com.rione.post.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataReactionIdJpaRepository extends JpaRepository<ReactionIdJpaEntity, Long> {
}
