package com.rione.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rione.user.infrastructure.persistence.entity.CityJpaEntity;

interface SpringDataCityJpaRepository extends JpaRepository<CityJpaEntity, Long> {
}
