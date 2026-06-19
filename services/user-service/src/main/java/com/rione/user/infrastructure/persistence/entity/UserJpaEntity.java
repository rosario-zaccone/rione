package com.rione.user.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 80)
	private String name;

	@Column(nullable = false, length = 80)
	private String surname;

	@Column(nullable = false, unique = true, length = 40)
	private String username;

	@Column(nullable = false, unique = true, length = 255)
	private String mail;

	@Column(nullable = false)
	private Long neighborhoodId;

	@Column(nullable = false)
	private LocalDateTime birthDate;

	@Column(nullable = false, length = 500)
	private String bio;

	@Column(nullable = false, length = 255)
	private String passwordHash;

	@Column(nullable = false)
	private boolean admin;

	protected UserJpaEntity() {
	}

	public UserJpaEntity(Long id, String name, String surname, String username, String mail, Long neighborhoodId,
			LocalDateTime birthDate, String bio, String passwordHash, boolean admin) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.username = username;
		this.mail = mail;
		this.neighborhoodId = neighborhoodId;
		this.birthDate = birthDate;
		this.bio = bio;
		this.passwordHash = passwordHash;
		this.admin = admin;
	}

	public Long id() {
		return id;
	}

	public String name() {
		return name;
	}

	public String surname() {
		return surname;
	}

	public String username() {
		return username;
	}

	public String mail() {
		return mail;
	}

	public Long neighborhoodId() {
		return neighborhoodId;
	}

	public LocalDateTime birthDate() {
		return birthDate;
	}

	public String bio() {
		return bio;
	}

	public String passwordHash() {
		return passwordHash;
	}

	public boolean admin() {
		return admin;
	}
}
