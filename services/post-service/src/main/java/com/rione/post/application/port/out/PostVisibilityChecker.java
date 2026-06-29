package com.rione.post.application.port.out;

import com.rione.common.application.OutPort;
import com.rione.post.domain.model.UserId;

@OutPort
public interface PostVisibilityChecker {

	RelationshipVisibility visibilityBetween(UserId viewer, UserId author);

	record RelationshipVisibility(boolean sameNeighborhood, boolean activeNeighborship, boolean blocked) {
	}
}
