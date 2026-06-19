package com.rione.user.infrastructure.persistence;

import java.util.Optional;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rione.user.infrastructure.persistence.entity.UserJpaEntity;

interface SpringDataUserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

	Optional<UserJpaEntity> findByMail(String mail);

	boolean existsByMail(String mail);

	boolean existsByUsername(String username);

	boolean existsByNeighborhoodIdIn(Collection<Long> neighborhoodIds);
}
