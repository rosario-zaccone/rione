package com.rione.social.application.port.out;

import com.rione.common.application.OutPort;
import com.rione.social.domain.model.NeighborRequest;

@OutPort
public interface SocialEventPublisher {

	void publishNeighborRequestReceived(NeighborRequest request);

	void publishNeighborRequestAccepted(NeighborRequest request);
}
