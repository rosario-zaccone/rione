package com.rione.social.infrastructure.persistence;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@Component
@Profile("local")
class LocalSocialDataResetInitializer implements ApplicationRunner {

	private final EntityManager entityManager;

	LocalSocialDataResetInitializer(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		entityManager
			.createNativeQuery("TRUNCATE TABLE neighbor_requests, neighborships, blocks RESTART IDENTITY")
			.executeUpdate();
	}
}
