package com.rione.user.domain.model;

import com.rione.common.domain.DDDAggregateRoot;

@DDDAggregateRoot
public class User {

	private final UserId id;
	private final Password password;
	private final boolean admin;
	private BirthDate birthDate;
	private FullName fullName;
	private Username username;
	private Mail mail;
	private Biography bio;
	private NeighborhoodId neighborhoodId;

	private User(UserId id, FullName fullName, Username username, Mail mail, NeighborhoodId neighborhoodId,
			BirthDate birthDate, Biography bio, Password password, boolean admin) {
		this.id = id;
		this.birthDate = birthDate;
		this.password = password;
		this.admin = admin;
		this.fullName = fullName;
		this.username = username;
		this.mail = mail;
		this.neighborhoodId = neighborhoodId;
		this.bio = bio;
	}

	public static User register(FullName fullName, Username username, Mail mail, NeighborhoodId neighborhoodId,
			BirthDate birthDate, Biography bio, Password password) {
		return new User(null, fullName, username, mail, neighborhoodId, birthDate, bio, password, false);
	}

	public static User restore(UserId id, FullName fullName, Username username, Mail mail, NeighborhoodId neighborhoodId,
			BirthDate birthDate, Biography bio, Password password, boolean admin) {
		return new User(id, fullName, username, mail, neighborhoodId, birthDate, bio, password, admin);
	}

	public void updateProfile(FullName fullName, Username username, NeighborhoodId neighborhoodId, BirthDate birthDate,
			Biography bio) {
		this.fullName = fullName;
		this.username = username;
		this.neighborhoodId = neighborhoodId;
		this.birthDate = birthDate;
		this.bio = bio;
	}

	public UserId id() {
		return id;
	}

	public BirthDate birthDate() {
		return birthDate;
	}

	public Password password() {
		return password;
	}

	public boolean isAdmin() {
		return admin;
	}

	public FullName fullName() {
		return fullName;
	}

	public Username username() {
		return username;
	}

	public Mail mail() {
		return mail;
	}

	public Biography bio() {
		return bio;
	}

	public NeighborhoodId neighborhoodId() {
		return neighborhoodId;
	}
}
