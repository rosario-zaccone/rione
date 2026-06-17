package com.rione.user.application.port.out;

import java.util.Optional;

import com.rione.common.application.OutPort;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;
import com.rione.user.domain.model.Username;

@OutPort
public interface UserRepository {

	UserId nextIdentity();

	User save(User user);

	Optional<User> findById(UserId userId);

	Optional<User> findByMail(Mail mail);

	boolean existsByMail(Mail mail);

	boolean existsByUsername(Username username);
}
