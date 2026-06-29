package com.rione.user.infrastructure.persistence;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rione.user.infrastructure.persistence.entity.UserJpaEntity;

interface SpringDataUserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

	Optional<UserJpaEntity> findByMail(String mail);

	boolean existsByMail(String mail);

	boolean existsByUsername(String username);

	boolean existsByNeighborhoodIdIn(Collection<Long> neighborhoodIds);

	long countByRegisteredAtGreaterThanEqual(LocalDateTime registeredAt);

	@Query("""
			select user from UserJpaEntity user
			where user.neighborhoodId = :neighborhoodId
			  and (lower(user.username) like lower(concat('%', :query, '%'))
			    or lower(user.name) like lower(concat('%', :query, '%'))
			    or lower(user.surname) like lower(concat('%', :query, '%')))
			order by user.username
			""")
	List<UserJpaEntity> searchByNeighborhood(@Param("neighborhoodId") Long neighborhoodId,
			@Param("query") String query);
}
