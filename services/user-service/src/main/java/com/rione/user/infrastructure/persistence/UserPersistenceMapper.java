package com.rione.user.infrastructure.persistence;

import com.rione.user.domain.model.Biography;
import com.rione.user.domain.model.BirthDate;
import com.rione.user.domain.model.FullName;
import com.rione.user.domain.model.Location;
import com.rione.user.domain.model.Mail;
import com.rione.user.domain.model.Neighborhood;
import com.rione.user.domain.model.NeighborhoodId;
import com.rione.user.domain.model.Password;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;
import com.rione.user.domain.model.Username;

class UserPersistenceMapper {

	UserJpaEntity toEntity(User user) {
		UserJpaEntity entity = new UserJpaEntity();
		entity.setId(user.id().value());
		entity.setName(user.fullName().name());
		entity.setSurname(user.fullName().surname());
		entity.setUsername(user.username().value());
		entity.setMail(user.mail().mail());
		entity.setNeighborhoodId(user.neighborhood().id().value());
		entity.setNeighborhoodName(user.neighborhood().name());
		entity.setCity(user.neighborhood().location().city());
		entity.setCountry(user.neighborhood().location().country());
		entity.setBirthDate(user.birthDate().value());
		entity.setBio(user.bio().info());
		entity.setPasswordHash(user.password().hash());
		entity.setAdmin(user.isAdmin());
		return entity;
	}

	User toDomain(UserJpaEntity entity) {
		User user = User.restore(new UserId(entity.getId()), new FullName(entity.getName(), entity.getSurname()),
				new Username(entity.getUsername()), new Mail(entity.getMail()),
				new Neighborhood(new NeighborhoodId(entity.getNeighborhoodId()), entity.getNeighborhoodName(),
						new Location(entity.getCity(), entity.getCountry())),
				new BirthDate(entity.getBirthDate()), new Biography(entity.getBio()),
				new Password(entity.getPasswordHash()), entity.isAdmin());
		return user;
	}
}
