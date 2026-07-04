package com.rione.post.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.domain.event.PostEventType;

import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import org.junit.jupiter.api.Test;

class PostMetricsConfigurationTest {

	@Test
	void exportsDashboardMetricNamesWithoutBaseUnitSuffixes() {
		PostEventStore eventStore = org.mockito.Mockito.mock(PostEventStore.class);
		when(eventStore.countEvents(PostEventType.POST_CREATED)).thenReturn(12L);
		when(eventStore.countEventsSince(any(), any())).thenReturn(3L);
		PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

		new PostMetricsConfiguration().postActivityMeterBinder(eventStore).bindTo(registry);

		String scrape = registry.scrape();
		assertThat(scrape).contains("rione_total_posts 12.0");
		assertThat(scrape).contains("rione_posts_created_today 3.0");
		assertThat(scrape).contains("rione_comments_created_today 3.0");
		assertThat(scrape).contains("rione_likes_created_today 3.0");
		assertThat(scrape).doesNotContain("rione_total_posts_posts");
		assertThat(scrape).doesNotContain("rione_comments_created_today_comments");
	}
}
