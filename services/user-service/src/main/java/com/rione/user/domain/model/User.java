package com.rione.user.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

@DDDAggregateRoot
public class User {

	private final UserId id;
	private final String passwordHash;
	private final boolean admin;
	private LocalDateTime birthDate;
	private FullName fullName;
	private Username username;
	private Mail mail;
	private Biography bio;
	private Neighborhood neighborhood;

	private User(UserId id, FullName fullName, Username username, Mail mail, Neighborhood neighborhood,
			LocalDateTime birthDate, Biography bio, String passwordHash, boolean admin) {
		if (id == null) {
			throw new DomainException("User id is required");
		}
		if (passwordHash == null || passwordHash.isBlank()) {
			throw new DomainException("Password hash is required");
		}
		this.id = id;
		this.birthDate = validateBirthDate(birthDate);
		this.passwordHash = passwordHash;
		this.admin = admin;
		this.fullName = Objects.requireNonNull(fullName, "fullName");
		this.username = Objects.requireNonNull(username, "username");
		this.mail = Objects.requireNonNull(mail, "mail");
		this.neighborhood = Objects.requireNonNull(neighborhood, "neighborhood");
		this.bio = bio == null ? new Biography("") : bio;
	}

	public static User register(UserId id, FullName fullName, Username username, Mail mail, Neighborhood neighborhood,
			LocalDateTime birthDate, Biography bio, String passwordHash) {
		return new User(id, fullName, username, mail, neighborhood, birthDate, bio, passwordHash, false);
	}

	public static User restore(UserId id, FullName fullName, Username username, Mail mail, Neighborhood neighborhood,
			LocalDateTime birthDate, Biography bio, String passwordHash, boolean admin) {
		return new User(id, fullName, username, mail, neighborhood, birthDate, bio, passwordHash, admin);
	}

	public void updateProfile(FullName fullName, Username username, Neighborhood neighborhood, LocalDateTime birthDate,
			Biography bio) {
		this.fullName = Objects.requireNonNull(fullName, "fullName");
		this.username = Objects.requireNonNull(username, "username");
		this.neighborhood = Objects.requireNonNull(neighborhood, "neighborhood");
		this.birthDate = validateBirthDate(birthDate);
		updateBio(bio);
	}

	public void updateBio(Biography bio) {
		this.bio = Objects.requireNonNull(bio, "bio");
	}

	public UserId id() {
		return id;
	}

	public LocalDateTime birthDate() {
		return birthDate;
	}

	public String passwordHash() {
		return passwordHash;
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

	private static LocalDateTime validateBirthDate(LocalDateTime birthDate) {
		if (birthDate == null || birthDate.isAfter(LocalDateTime.now())) {
			throw new DomainException("Birth date must be in the past");
		}
		return birthDate;
	}
}
