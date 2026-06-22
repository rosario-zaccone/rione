package com.rione.social.application.port.out;

import java.util.List;

import com.rione.common.application.OutPort;
import com.rione.social.domain.model.UserId;

@OutPort
public interface UserDirectory {

	List<UserProfile> searchInNeighborhood(UserId requester, String query);

	record UserProfile(UserId id, String name, String surname, String username) {
	}
}
