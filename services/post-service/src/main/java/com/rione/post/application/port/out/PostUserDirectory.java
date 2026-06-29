package com.rione.post.application.port.out;

import java.util.Collection;
import java.util.Map;

import com.rione.common.application.OutPort;
import com.rione.post.domain.model.UserId;

@OutPort
public interface PostUserDirectory {

	Map<UserId, UserProfile> findByIds(Collection<UserId> userIds);

	record UserProfile(UserId id, String name, String surname, String username) {
	}
}
