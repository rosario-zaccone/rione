package com.rione.post.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCommentIdJpaRepository extends JpaRepository<CommentIdJpaEntity, Long> {
}
