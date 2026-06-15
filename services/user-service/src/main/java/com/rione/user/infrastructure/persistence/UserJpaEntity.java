package com.rione.user.infrastructure.persistence;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
class UserJpaEntity {

	@Id
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

	@Column(nullable = false, length = 120)
	private String neighborhoodName;

	@Column(nullable = false, length = 80)
	private String city;

	@Column(nullable = false, length = 80)
	private String country;

	@Column(nullable = false)
	private LocalDateTime birthDate;

	@Column(nullable = false, length = 500)
	private String bio;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private boolean admin;

	protected UserJpaEntity() {
	}

	Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	String getSurname() {
		return surname;
	}

	void setSurname(String surname) {
		this.surname = surname;
	}

	String getUsername() {
		return username;
	}

	void setUsername(String username) {
		this.username = username;
	}

	String getMail() {
		return mail;
	}

	void setMail(String mail) {
		this.mail = mail;
	}

	Long getNeighborhoodId() {
		return neighborhoodId;
	}

	void setNeighborhoodId(Long neighborhoodId) {
		this.neighborhoodId = neighborhoodId;
	}

	String getNeighborhoodName() {
		return neighborhoodName;
	}

	void setNeighborhoodName(String neighborhoodName) {
		this.neighborhoodName = neighborhoodName;
	}

	String getCity() {
		return city;
	}

	void setCity(String city) {
		this.city = city;
	}

	String getCountry() {
		return country;
	}

	void setCountry(String country) {
		this.country = country;
	}

	LocalDateTime getBirthDate() {
		return birthDate;
	}

	void setBirthDate(LocalDateTime birthDate) {
		this.birthDate = birthDate;
	}

	String getBio() {
		return bio;
	}

	void setBio(String bio) {
		this.bio = bio;
	}

	String getPasswordHash() {
		return passwordHash;
	}

	void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	boolean isAdmin() {
		return admin;
	}

	void setAdmin(boolean admin) {
		this.admin = admin;
	}

}
