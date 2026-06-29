package com.rione.post.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataPostIdJpaRepository extends JpaRepository<PostIdJpaEntity, Long> {
}
