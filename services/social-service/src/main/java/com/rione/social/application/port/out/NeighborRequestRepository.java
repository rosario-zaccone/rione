package com.rione.social.application.port.out;

import com.rione.common.application.OutPort;
import java.util.List;
import java.util.Optional;

import com.rione.social.domain.model.NeighborRequest;
import com.rione.social.domain.model.NeighborRequestId;
import com.rione.social.domain.model.UserId;

@OutPort
public interface NeighborRequestRepository {

	NeighborRequest save(NeighborRequest request);

	Optional<NeighborRequest> findById(NeighborRequestId id);

	boolean existsPendingBetween(UserId sender, UserId receiver);

	List<NeighborRequest> findPendingInvolving(UserId userId);

	void delete(NeighborRequestId id);
}
