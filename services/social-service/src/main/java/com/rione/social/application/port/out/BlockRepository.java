package com.rione.social.application.port.out;

import com.rione.common.application.OutPort;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.BlockId;
import com.rione.social.domain.model.UserId;

@OutPort
public interface BlockRepository {

	Block save(Block block);

	boolean existsBetween(UserId blocker, UserId blocked);

	void deleteBetween(UserId blocker, UserId blocked);
}
