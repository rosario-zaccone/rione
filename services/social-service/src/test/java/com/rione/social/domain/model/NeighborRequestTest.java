package com.rione.social.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class NeighborRequestTest {

	@Test
	void acceptsPendingRequest() {
		NeighborRequest request = request();

		request.accept();

		assertEquals(RequestStatus.ACCEPTED, request.status());
	}

	@Test
	void rejectsPendingRequest() {
		NeighborRequest request = request();

		request.reject();

		assertEquals(RequestStatus.REJECTED, request.status());
	}

	@Test
	void rejectsStatusChangesWhenNotPending() {
		NeighborRequest request = request();
		request.accept();

		assertThrows(DomainException.class, request::reject);
	}

	@Test
	void rejectsSelfRequest() {
		UserId user = new UserId(1L);

		assertThrows(DomainException.class,
				() -> NeighborRequest.create(new NeighborRequestId(1L), user, user, LocalDateTime.now()));
	}

	private NeighborRequest request() {
		return NeighborRequest.create(new NeighborRequestId(1L), new UserId(1L), new UserId(2L),
				LocalDateTime.of(2026, 6, 17, 0, 0));
	}
}
