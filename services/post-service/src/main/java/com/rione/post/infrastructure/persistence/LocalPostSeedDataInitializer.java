package com.rione.post.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rione.post.application.port.out.PostEventStore;
import com.rione.post.domain.event.CommentAdded;
import com.rione.post.domain.event.PostCreated;
import com.rione.post.domain.event.ReactionAdded;
import com.rione.post.domain.model.CommentContent;
import com.rione.post.domain.model.CommentId;
import com.rione.post.domain.model.NeighborhoodId;
import com.rione.post.domain.model.Place;
import com.rione.post.domain.model.PostContent;
import com.rione.post.domain.model.PostId;
import com.rione.post.domain.model.PostType;
import com.rione.post.domain.model.PostVisibility;
import com.rione.post.domain.model.ReactionId;
import com.rione.post.domain.model.ReactionType;
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
		resetPostStore();
		for (SeedPost seed : seedPosts()) {
			PostId postId = eventStore.nextPostId();
			PostCreated created = new PostCreated(postId, new UserId(seed.authorId()),
					new NeighborhoodId(seed.neighborhoodId()), new PostContent(seed.content()), seed.place(),
					seed.type(), PostVisibility.PUBLIC, seed.createdAt());
			eventStore.append(postId, 0, List.of(created));
			long version = 1;

			for (SeedComment comment : seed.comments()) {
				CommentId commentId = eventStore.nextCommentId();
				CommentAdded commentAdded = new CommentAdded(postId, commentId, new UserId(comment.authorId()),
						new CommentContent(comment.content()), comment.occurredAt());
				eventStore.append(postId, version, List.of(commentAdded));
				version++;
			}

			for (SeedReaction reaction : seed.reactions()) {
				ReactionId reactionId = eventStore.nextReactionId();
				ReactionAdded reactionAdded = new ReactionAdded(postId, reactionId, new UserId(reaction.authorId()),
						reaction.type(), reaction.occurredAt());
				eventStore.append(postId, version, List.of(reactionAdded));
				version++;
			}
		}
	}

	private void resetPostStore() {
		entityManager
			.createNativeQuery(
					"TRUNCATE TABLE post_events, post_id_sequence, comment_id_sequence, reaction_id_sequence RESTART IDENTITY")
			.executeUpdate();
	}

	private static List<SeedPost> seedPosts() {
		return List.of(
				new SeedPost(1L, 1L,
						"Domani mattina passo dal mercato di via XXI Aprile. Se a qualcuno serve una piccola commissione, scrivetemi entro stasera.",
						PostType.HELP, new Place(11.319118, 44.492351), LocalDateTime.of(2026, 6, 20, 8, 30),
						List.of(new SeedComment(4L, "Ci sto, posso venire poco dopo le nove!",
								LocalDateTime.of(2026, 6, 20, 9, 0))),
						List.of(new SeedReaction(6L, ReactionType.UPVOTE, LocalDateTime.of(2026, 6, 20, 9, 15)))),
				new SeedPost(3L, 1L,
						"Sabato vorrei organizzare uno scambio libri sotto il portico vicino a Porta Saragozza. Ognuno porta due libri e una storia.",
						PostType.EVENT, new Place(11.326799, 44.491427), LocalDateTime.of(2026, 6, 21, 18, 15),
						List.of(new SeedComment(9L, "Bellissima idea, porto due romanzi che ho appena finito.",
								LocalDateTime.of(2026, 6, 21, 19, 0))),
						List.of(new SeedReaction(1L, ReactionType.UPVOTE, LocalDateTime.of(2026, 6, 21, 19, 5)),
								new SeedReaction(5L, ReactionType.UPVOTE, LocalDateTime.of(2026, 6, 21, 19, 10)))),
				new SeedPost(6L, 1L,
						"Segnalo lavori in via Saragozza alta: il marciapiede lato portico e piu stretto del solito, meglio passare con calma.",
						PostType.WARNING, new Place(11.314812, 44.490889), LocalDateTime.of(2026, 6, 22, 9, 5),
						List.of(new SeedComment(2L, "Grazie della segnalazione, evito quella zona in bici.",
								LocalDateTime.of(2026, 6, 22, 9, 30))),
						List.of(new SeedReaction(8L, ReactionType.UPVOTE, LocalDateTime.of(2026, 6, 22, 9, 45)))),
				new SeedPost(11L, 2L,
						"Domenica al parco della Zucca ci troviamo per una merenda condivisa. Chi passa puo portare una coperta o qualcosa da bere.",
						PostType.EVENT, new Place(11.348942, 44.521534), LocalDateTime.of(2026, 6, 20, 16, 45),
						List.of(new SeedComment(19L, "Ci saro, porto anche dei biscotti fatti in casa.",
								LocalDateTime.of(2026, 6, 20, 17, 10))),
						List.of(new SeedReaction(14L, ReactionType.UPVOTE, LocalDateTime.of(2026, 6, 20, 17, 20)))),
				new SeedPost(14L, 2L,
						"Ho alcune cassette di legno pulite da regalare, utili per balconi o piccoli scaffali. Ritiro in zona Bolognina.",
						PostType.DISCUSSION, new Place(11.349887, 44.514612), LocalDateTime.of(2026, 6, 23, 12, 20),
						List.of(new SeedComment(16L, "Le prendo io volentieri, mi servono proprio per il balcone.",
								LocalDateTime.of(2026, 6, 23, 13, 0))),
						List.of(new SeedReaction(18L, ReactionType.UPVOTE, LocalDateTime.of(2026, 6, 23, 13, 5)))),
				new SeedPost(19L, 2L,
						"Questa settimana raccolgo proposte per migliorare la bacheca del condominio. Idee semplici e concrete sono benvenute.",
						PostType.HELP, null, LocalDateTime.of(2026, 6, 24, 19, 10),
						List.of(new SeedComment(12L, "Ottima iniziativa, serve una bacheca digitale condivisa.",
								LocalDateTime.of(2026, 6, 24, 19, 40))),
						List.of(new SeedReaction(11L, ReactionType.UPVOTE, LocalDateTime.of(2026, 6, 24, 19, 45)),
								new SeedReaction(20L, ReactionType.UPVOTE, LocalDateTime.of(2026, 6, 24, 19, 50)))));
	}

	private record SeedPost(Long authorId, Long neighborhoodId, String content, PostType type, Place place,
			LocalDateTime createdAt, List<SeedComment> comments, List<SeedReaction> reactions) {
	}

	private record SeedComment(Long authorId, String content, LocalDateTime occurredAt) {
	}

	private record SeedReaction(Long authorId, ReactionType type, LocalDateTime occurredAt) {
	}
}
