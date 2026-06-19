package com.rione.social.domain.model;

import com.rione.common.domain.DDDAggregateRoot;

import java.time.LocalDateTime;

@DDDAggregateRoot
public class NeighborRequest {

	private final NeighborRequestId id;
	private final UserId sender;
	private final UserId receiver;
	private final LocalDateTime date;
	private RequestStatus status;

	private NeighborRequest(NeighborRequestId id, UserId sender, UserId receiver, LocalDateTime date,
			RequestStatus status) {
		if (sender.equals(receiver)) {
			throw new DomainException("Neighbor request sender and receiver must be different users");
		}
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.date = date == null ? LocalDateTime.now() : date;
		this.status = status == null ? RequestStatus.PENDING : status;
	}

	public static NeighborRequest create(UserId sender, UserId receiver, LocalDateTime date) {
		return new NeighborRequest(null, sender, receiver, date, RequestStatus.PENDING);
	}

	public static NeighborRequest restore(NeighborRequestId id, UserId sender, UserId receiver, LocalDateTime date,
			RequestStatus status) {
		return new NeighborRequest(id, sender, receiver, date, status);
	}

	public void accept() {
		ensurePending("Only pending neighbor requests can be accepted");
		status = RequestStatus.ACCEPTED;
	}

	public void reject() {
		ensurePending("Only pending neighbor requests can be rejected");
		status = RequestStatus.REJECTED;
	}

	private void ensurePending(String message) {
		if (status != RequestStatus.PENDING) {
			throw new DomainException(message);
		}
	}

	public NeighborRequestId id() {
		return id;
	}

	public UserId sender() {
		return sender;
	}

	public UserId receiver() {
		return receiver;
	}

	public LocalDateTime date() {
		return date;
	}

	public RequestStatus status() {
		return status;
	}

	public boolean involves(UserId userId) {
		return sender.equals(userId) || receiver.equals(userId);
	}

	public UserId counterpartOf(UserId userId) {
		if (sender.equals(userId)) {
			return receiver;
		}
		if (receiver.equals(userId)) {
			return sender;
		}
		throw new DomainException("Neighbor request does not involve user");
	}
}
