package com.rione.social.application.port.out;

import java.util.List;
import java.util.Optional;

import com.rione.common.application.OutPort;
import com.rione.social.domain.model.Block;
import com.rione.social.domain.model.UserId;

@OutPort
public interface BlockRepository {

	Block save(Block block);

	List<Block> findByBlocker(UserId blocker);

	Optional<Block> findBetween(UserId blocker, UserId blocked);

	boolean existsBetween(UserId blocker, UserId blocked);

	void deleteBetween(UserId blocker, UserId blocked);
}
