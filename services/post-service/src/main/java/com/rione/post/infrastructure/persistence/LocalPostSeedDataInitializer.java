package com.rione.post.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.domain.event.PostCreated;
import com.rione.post.domain.model.NeighborhoodId;
import com.rione.post.domain.model.Place;
import com.rione.post.domain.model.PostContent;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.PostType;
import com.rione.post.domain.model.PostVisibility;
import com.rione.post.domain.model.UserId;

import jakarta.persistence.EntityManager;

@Component
@Profile("local")
class LocalPostSeedDataInitializer implements ApplicationRunner {

	private final EntityManager entityManager;
	private final PostEventStore eventStore;

	LocalPostSeedDataInitializer(EntityManager entityManager, PostEventStore eventStore) {
		this.entityManager = entityManager;
		this.eventStore = eventStore;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (!isPostStoreEmpty()) {
			return;
		}
		for (SeedPost seed : seedPosts()) {
			PostId postId = eventStore.nextPostId();
			PostCreated event = new PostCreated(postId, new UserId(seed.authorId()),
					new NeighborhoodId(seed.neighborhoodId()), new PostContent(seed.content()), seed.place(),
					seed.type(), PostVisibility.PUBLIC, seed.createdAt());
			eventStore.append(postId, 0, List.of(event));
		}
	}

	private boolean isPostStoreEmpty() {
		Number eventCount = (Number) entityManager.createNativeQuery("SELECT COUNT(*) FROM post_events").getSingleResult();
		return eventCount.longValue() == 0;
	}

	private static List<SeedPost> seedPosts() {
		return List.of(
				new SeedPost(1L, 1L,
						"Domani mattina passo dal mercato di via XXI Aprile. Se a qualcuno serve una piccola commissione, scrivetemi entro stasera.",
						PostType.HELP, new Place(11.319118, 44.492351), LocalDateTime.of(2026, 6, 20, 8, 30)),
				new SeedPost(3L, 1L,
						"Sabato vorrei organizzare uno scambio libri sotto il portico vicino a Porta Saragozza. Ognuno porta due libri e una storia.",
						PostType.EVENT, new Place(11.326799, 44.491427), LocalDateTime.of(2026, 6, 21, 18, 15)),
				new SeedPost(6L, 1L,
						"Segnalo lavori in via Saragozza alta: il marciapiede lato portico e piu stretto del solito, meglio passare con calma.",
						PostType.WARNING, new Place(11.314812, 44.490889), LocalDateTime.of(2026, 6, 22, 9, 5)),
				new SeedPost(11L, 2L,
						"Domenica al parco della Zucca ci troviamo per una merenda condivisa. Chi passa puo portare una coperta o qualcosa da bere.",
						PostType.EVENT, new Place(11.348942, 44.521534), LocalDateTime.of(2026, 6, 20, 16, 45)),
				new SeedPost(14L, 2L,
						"Ho alcune cassette di legno pulite da regalare, utili per balconi o piccoli scaffali. Ritiro in zona Bolognina.",
						PostType.DISCUSSION, new Place(11.349887, 44.514612), LocalDateTime.of(2026, 6, 23, 12, 20)),
				new SeedPost(19L, 2L,
						"Questa settimana raccolgo proposte per migliorare la bacheca del condominio. Idee semplici e concrete sono benvenute.",
						PostType.HELP, null, LocalDateTime.of(2026, 6, 24, 19, 10)));
	}

	private record SeedPost(Long authorId, Long neighborhoodId, String content, PostType type, Place place,
			LocalDateTime createdAt) {
	}
}
