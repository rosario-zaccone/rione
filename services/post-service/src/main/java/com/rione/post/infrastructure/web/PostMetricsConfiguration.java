package com.rione.post.infrastructure.web;

import java.time.LocalDate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.domain.event.PostEventType;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.binder.MeterBinder;

@Configuration
class PostMetricsConfiguration {

	@Bean
	MeterBinder postActivityMeterBinder(PostEventStore eventStore) {
		return registry -> {
			Gauge.builder("rione_total_posts", eventStore,
					store -> store.countEvents(PostEventType.POST_CREATED))
				.description("Total posts created")
				.register(registry);
			Gauge.builder("rione_posts_created_today", eventStore,
					store -> store.countEventsSince(PostEventType.POST_CREATED, LocalDate.now()))
				.description("Posts created since the start of the current service day")
				.register(registry);
			Gauge.builder("rione_comments_created_today", eventStore,
					store -> store.countEventsSince(PostEventType.COMMENT_ADDED, LocalDate.now()))
				.description("Comments created since the start of the current service day")
				.register(registry);
			Gauge.builder("rione_likes_created_today", eventStore,
					store -> store.countEventsSince(PostEventType.REACTION_ADDED, LocalDate.now()))
				.description("Likes and reactions created since the start of the current service day")
				.register(registry);
		};
	}
}
