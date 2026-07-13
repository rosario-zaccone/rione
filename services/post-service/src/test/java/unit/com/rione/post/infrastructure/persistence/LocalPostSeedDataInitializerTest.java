package com.rione.post.infrastructure.persistence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.ApplicationArguments;

import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.domain.model.CommentId;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.ReactionId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

class LocalPostSeedDataInitializerTest {

	@Test
	void resetsAndReseedsDemoPostsOnEveryStartup() {
		EntityManager entityManager = mock(EntityManager.class);
		PostEventStore eventStore = mock(PostEventStore.class);
		Query truncateQuery = mock(Query.class);
		when(entityManager.createNativeQuery(contains("TRUNCATE TABLE post_events"))).thenReturn(truncateQuery);
		when(eventStore.nextPostId()).thenReturn(new PostId(1L), new PostId(2L), new PostId(3L), new PostId(4L),
				new PostId(5L), new PostId(6L));
		when(eventStore.nextCommentId()).thenReturn(new CommentId(1L), new CommentId(2L), new CommentId(3L),
				new CommentId(4L), new CommentId(5L), new CommentId(6L));
		when(eventStore.nextReactionId()).thenReturn(new ReactionId(1L), new ReactionId(2L), new ReactionId(3L),
				new ReactionId(4L), new ReactionId(5L), new ReactionId(6L), new ReactionId(7L), new ReactionId(8L));

		new LocalPostSeedDataInitializer(entityManager, eventStore).run(mock(ApplicationArguments.class));

		verify(truncateQuery, times(1)).executeUpdate();
		verify(eventStore, times(6)).nextPostId();
		verify(eventStore, times(6)).nextCommentId();
		verify(eventStore, times(8)).nextReactionId();
		verify(eventStore, times(20)).append(any(PostId.class), anyLong(), any());
	}
}
