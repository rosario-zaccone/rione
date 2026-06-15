package com.rione.user.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface SpringDataUserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

	Optional<UserJpaEntity> findByMail(String mail);

	boolean existsByMail(String mail);

	boolean existsByUsername(String username);

	@Query("""
			select user
			from UserJpaEntity user
			where (:neighborhoodId is null or user.neighborhoodId = :neighborhoodId)
			  and (
			    :query = ''
			    or lower(concat(user.name, ' ', user.surname)) like concat('%', :query, '%')
			    or lower(user.username) like concat('%', :query, '%')
			    or lower(user.neighborhoodName) like concat('%', :query, '%')
			  )
			""")
	List<UserJpaEntity> search(@Param("query") String query, @Param("neighborhoodId") Long neighborhoodId);
}
