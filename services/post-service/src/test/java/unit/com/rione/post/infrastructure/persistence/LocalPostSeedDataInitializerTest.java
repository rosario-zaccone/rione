package com.rione.post.infrastructure.persistence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.domain.model.PostId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

class LocalPostSeedDataInitializerTest {

	@Test
	void keepsExistingLocalPostsOnStartup() {
		EntityManager entityManager = mock(EntityManager.class);
		PostEventStore eventStore = mock(PostEventStore.class);
		whenPostEventCount(entityManager, 1L);

		new LocalPostSeedDataInitializer(entityManager, eventStore).run(mock(ApplicationArguments.class));

		verify(eventStore, never()).nextPostId();
		verify(eventStore, never()).append(any(), anyLong(), any());
	}

	@Test
	void seedsDemoPostsWhenPostStoreIsEmpty() {
		EntityManager entityManager = mock(EntityManager.class);
		PostEventStore eventStore = mock(PostEventStore.class);
		whenPostEventCount(entityManager, 0L);
		when(eventStore.nextPostId()).thenReturn(new PostId(1L), new PostId(2L), new PostId(3L), new PostId(4L),
				new PostId(5L), new PostId(6L));

		new LocalPostSeedDataInitializer(entityManager, eventStore).run(mock(ApplicationArguments.class));

		verify(eventStore, times(6)).append(any(PostId.class), eq(0L),
				argThat(events -> events != null && events.size() == 1));
	}

	private static void whenPostEventCount(EntityManager entityManager, long count) {
		Query query = mock(Query.class);
		when(entityManager.createNativeQuery("SELECT COUNT(*) FROM post_events")).thenReturn(query);
		when(query.getSingleResult()).thenReturn(count);
	}
}
