INSERT INTO cities (name)
SELECT 'Bologna'
WHERE NOT EXISTS (
	SELECT 1
	FROM cities
	WHERE name = 'Bologna'
);

INSERT INTO neighborhoods (name, city_id)
SELECT 'Saragozza', c.id
FROM cities c
WHERE c.name = 'Bologna'
	AND NOT EXISTS (
		SELECT 1
		FROM neighborhoods n
		WHERE n.name = 'Saragozza'
			AND n.city_id = c.id
	);

INSERT INTO neighborhoods (name, city_id)
SELECT 'Navile', c.id
FROM cities c
WHERE c.name = 'Bologna'
	AND NOT EXISTS (
		SELECT 1
		FROM neighborhoods n
		WHERE n.name = 'Navile'
			AND n.city_id = c.id
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Luigi', 'Verdi', 'luigino', 'luigi@example.com', n.id,
	TIMESTAMP '2020-06-19 17:54:46.972',
	'I help neighbors with local errands and daily coordination.',
	'pbkdf2$120000$NknZqdlBgrDpRcysbAvKFg==$8c8c1a094febf691b489e1af0d9c6434384c169d5b4212a2294742ee231997af',
	FALSE
FROM neighborhoods n
WHERE n.name = 'Saragozza'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'luigi@example.com'
			OR u.username = 'luigino'
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Admin', 'Rione', 'admin', 'admin@rione.test', n.id,
	TIMESTAMP '1980-01-01 00:00:00',
	'Local administrator for Rione testing.',
	'pbkdf2$120000$/5mG566UBdIMVfvEw51dhA==$1814fe0e4458ddb960e60cea0c5bad2c3e048fd1fd11e8a8f5630af2b83b9fa3',
	TRUE
FROM neighborhoods n
WHERE n.name = 'Saragozza'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'admin@rione.test'
			OR u.username = 'admin'
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Sara', 'One', 'saragozza.one', 'saragozza.one@rione.test', n.id,
	TIMESTAMP '1990-01-01 00:00:00',
	'Seed user living in Saragozza for local tests.',
	'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
	FALSE
FROM neighborhoods n
WHERE n.name = 'Saragozza'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'saragozza.one@rione.test'
			OR u.username = 'saragozza.one'
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Sergio', 'Two', 'saragozza.two', 'saragozza.two@rione.test', n.id,
	TIMESTAMP '1991-01-01 00:00:00',
	'Second seed user living in Saragozza for tests.',
	'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
	FALSE
FROM neighborhoods n
WHERE n.name = 'Saragozza'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'saragozza.two@rione.test'
			OR u.username = 'saragozza.two'
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Nadia', 'One', 'navile.one', 'navile.one@rione.test', n.id,
	TIMESTAMP '1992-01-01 00:00:00',
	'Seed user living in Navile for local tests.',
	'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
	FALSE
FROM neighborhoods n
WHERE n.name = 'Navile'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'navile.one@rione.test'
			OR u.username = 'navile.one'
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Nico', 'Two', 'navile.two', 'navile.two@rione.test', n.id,
	TIMESTAMP '1993-01-01 00:00:00',
	'Second seed user living in Navile for tests.',
	'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
	FALSE
FROM neighborhoods n
WHERE n.name = 'Navile'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'navile.two@rione.test'
			OR u.username = 'navile.two'
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Anna', 'Bianchi', 'anna.bianchi', 'anna.bianchi@rione.test', n.id,
	TIMESTAMP '1991-04-12 00:00:00',
	'I organize evening walks and reading circles for the block.',
	'pbkdf2$120000$UmlvbmVMb2NhbFNhbHQxMg==$bffc3b9f4f15606c3fa6e06202aeb5fca20cdeb17c990c4c280d5b2135a38980',
	FALSE
FROM neighborhoods n
WHERE n.name = 'Saragozza'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'anna.bianchi@rione.test'
			OR u.username = 'anna.bianchi'
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Marco', 'Conti', 'marco.conti', 'marco.conti@rione.test', n.id,
	TIMESTAMP '1989-09-03 00:00:00',
	'I keep the courtyard tools in order and help with small repairs.',
	'pbkdf2$120000$UmlvbmVMb2NhbFNhbHQxMg==$bffc3b9f4f15606c3fa6e06202aeb5fca20cdeb17c990c4c280d5b2135a38980',
	FALSE
FROM neighborhoods n
WHERE n.name = 'Saragozza'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'marco.conti@rione.test'
			OR u.username = 'marco.conti'
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Giulia', 'Rinaldi', 'giulia.rinaldi', 'giulia.rinaldi@rione.test', n.id,
	TIMESTAMP '1993-02-18 00:00:00',
	'I coordinate neighborhood dinners and shared garden updates.',
	'pbkdf2$120000$UmlvbmVMb2NhbFNhbHQxMg==$bffc3b9f4f15606c3fa6e06202aeb5fca20cdeb17c990c4c280d5b2135a38980',
	FALSE
FROM neighborhoods n
WHERE n.name = 'Navile'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'giulia.rinaldi@rione.test'
			OR u.username = 'giulia.rinaldi'
	);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
SELECT 'Paolo', 'Moretti', 'paolo.moretti', 'paolo.moretti@rione.test', n.id,
	TIMESTAMP '1987-11-27 00:00:00',
	'I help neighbors with bikes, errands, and local message boards.',
	'pbkdf2$120000$UmlvbmVMb2NhbFNhbHQxMg==$bffc3b9f4f15606c3fa6e06202aeb5fca20cdeb17c990c4c280d5b2135a38980',
	FALSE
FROM neighborhoods n
WHERE n.name = 'Navile'
	AND NOT EXISTS (
		SELECT 1
		FROM users u
		WHERE u.mail = 'paolo.moretti@rione.test'
			OR u.username = 'paolo.moretti'
	);
