package com.rione.social.application.port.out;

import com.rione.common.application.OutPort;
import com.rione.social.domain.model.UserId;

@OutPort
public interface NeighborhoodMembership {

	boolean sameNeighborhood(UserId firstUser, UserId secondUser);
}
