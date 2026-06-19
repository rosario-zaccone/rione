package com.rione.user.application.port.out;

import java.util.Collection;
import java.util.Optional;

import com.rione.common.application.OutPort;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.NeighborhoodId;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;
import com.rione.user.domain.model.Username;

@OutPort
public interface UserRepository {

	User save(User user);

	Optional<User> findById(UserId userId);

	Optional<User> findByMail(Mail mail);

	boolean existsByMail(Mail mail);

	boolean existsByUsername(Username username);

	boolean existsByNeighborhoodIdIn(Collection<NeighborhoodId> neighborhoodIds);
}
