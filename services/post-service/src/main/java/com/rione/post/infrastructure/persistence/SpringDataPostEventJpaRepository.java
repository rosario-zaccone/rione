package com.rione.post.infrastructure.persistence;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rione.post.domain.event.PostEventType;

interface SpringDataPostEventJpaRepository extends JpaRepository<PostEventJpaEntity, Long> {

	List<PostEventJpaEntity> findByPostIdOrderByVersionAsc(Long postId);

	List<PostEventJpaEntity> findAllByOrderByPostIdAscVersionAsc();

	long countByPostId(Long postId);

	long countByType(PostEventType type);

	long countByTypeAndOccurredAtGreaterThanEqual(PostEventType type, LocalDateTime occurredAt);
}
