package com.rione.social.application.port.out;

import com.rione.common.application.OutPort;
import java.util.List;

import com.rione.social.domain.model.Neighborship;
import com.rione.social.domain.model.NeighborshipId;
import com.rione.social.domain.model.UserId;

@OutPort
public interface NeighborshipRepository {

	NeighborshipId nextIdentity();

	Neighborship save(Neighborship neighborship);

	List<Neighborship> findByFollower(UserId follower);

	boolean exists(UserId follower, UserId followed);

	void deleteBetween(UserId firstUser, UserId secondUser);
}
