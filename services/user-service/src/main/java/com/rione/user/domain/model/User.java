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
	private Neighborhood neighborhood;

	private User(UserId id, FullName fullName, Username username, Mail mail, Neighborhood neighborhood,
			BirthDate birthDate, Biography bio, Password password, boolean admin) {
		this.id = id;
		this.birthDate = birthDate;
		this.password = password;
		this.admin = admin;
		this.fullName = fullName;
		this.username = username;
		this.mail = mail;
		this.neighborhood = neighborhood;
		this.bio = bio;
	}

	public static User register(UserId id, FullName fullName, Username username, Mail mail, Neighborhood neighborhood,
			BirthDate birthDate, Biography bio, Password password) {
		return new User(id, fullName, username, mail, neighborhood, birthDate, bio, password, false);
	}

	public static User restore(UserId id, FullName fullName, Username username, Mail mail, Neighborhood neighborhood,
			BirthDate birthDate, Biography bio, Password password, boolean admin) {
		return new User(id, fullName, username, mail, neighborhood, birthDate, bio, password, admin);
	}

	public void updateProfile(FullName fullName, Username username, Neighborhood neighborhood, BirthDate birthDate,
			Biography bio) {
		this.fullName = fullName;
		this.username = username;
		this.neighborhood = neighborhood;
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

	public Neighborhood neighborhood() {
		return neighborhood;
	}
}
