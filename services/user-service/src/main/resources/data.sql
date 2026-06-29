TRUNCATE TABLE users, neighborhoods, cities RESTART IDENTITY CASCADE;

INSERT INTO cities (name)
VALUES ('Bologna');

INSERT INTO neighborhoods (name, city_id)
VALUES
	('Saragozza', 1),
	('Navile', 1);

INSERT INTO users (name, surname, username, mail, neighborhood_id, birth_date, bio, password_hash, admin)
VALUES
	(
		'Elena',
		'Ferri',
		'elena.ferri',
		'elena.ferri@rione.test',
		1,
		TIMESTAMP '1988-03-14 00:00:00',
		'Vivo vicino a via Saragozza, mi piace organizzare passeggiate serali e scambi di libri tra vicini.',
		'pbkdf2$120000$/5mG566UBdIMVfvEw51dhA==$1814fe0e4458ddb960e60cea0c5bad2c3e048fd1fd11e8a8f5630af2b83b9fa3',
		TRUE
	),
	(
		'Luca',
		'Martelli',
		'luca.martelli',
		'luca.martelli@rione.test',
		1,
		TIMESTAMP '1985-07-22 00:00:00',
		'Riparo biciclette nel tempo libero e tengo d''occhio gli annunci utili per chi vive sotto i portici.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Chiara',
		'Galli',
		'chiara.galli',
		'chiara.galli@rione.test',
		1,
		TIMESTAMP '1992-11-05 00:00:00',
		'Condivido consigli su mercati, biblioteche e piccole iniziative culturali nel quartiere Saragozza.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Matteo',
		'Rossi',
		'matteo.rossi',
		'matteo.rossi@rione.test',
		1,
		TIMESTAMP '1990-01-19 00:00:00',
		'Abito in zona Meloncello, seguo eventi sportivi locali e aiuto volentieri per commissioni rapide.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Francesca',
		'Barbieri',
		'francesca.barbieri',
		'francesca.barbieri@rione.test',
		1,
		TIMESTAMP '1979-09-28 00:00:00',
		'Curo piante da balcone e scambio talee, semi e consigli pratici con chi vive nelle vie vicine.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Andrea',
		'Montanari',
		'andrea.montanari',
		'andrea.montanari@rione.test',
		1,
		TIMESTAMP '1983-12-02 00:00:00',
		'Segnalo lavori stradali, variazioni bus e occasioni per rendere piu semplice la vita di quartiere.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Giulia',
		'Neri',
		'giulia.neri',
		'giulia.neri@rione.test',
		1,
		TIMESTAMP '1995-04-16 00:00:00',
		'Lavoro da casa e mi interessa creare una rete tranquilla per prestiti, consigli e supporto quotidiano.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Paolo',
		'Bianchi',
		'paolo.bianchi',
		'paolo.bianchi@rione.test',
		1,
		TIMESTAMP '1976-06-30 00:00:00',
		'Conosco bene negozi storici e percorsi pedonali, mi piace aiutare i nuovi arrivati a orientarsi.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Martina',
		'Conti',
		'martina.conti',
		'martina.conti@rione.test',
		1,
		TIMESTAMP '1998-08-09 00:00:00',
		'Studio e vivo in Saragozza, cerco persone con cui condividere segnalazioni e piccole iniziative.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Roberto',
		'De Santis',
		'roberto.desantis',
		'roberto.desantis@rione.test',
		1,
		TIMESTAMP '1981-10-11 00:00:00',
		'Mi occupo spesso di spesa condivisa e recupero oggetti utili prima che vengano buttati via.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Valentina',
		'Ricci',
		'valentina.ricci',
		'valentina.ricci@rione.test',
		2,
		TIMESTAMP '1987-02-24 00:00:00',
		'Abito in Bolognina, seguo mercati, eventi di strada e iniziative per famiglie nel Navile.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Simone',
		'Rinaldi',
		'simone.rinaldi',
		'simone.rinaldi@rione.test',
		2,
		TIMESTAMP '1984-05-07 00:00:00',
		'Mi muovo spesso in bici lungo il canale Navile e condivido informazioni su viabilita e rastrelliere.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Federica',
		'Lombardi',
		'federica.lombardi',
		'federica.lombardi@rione.test',
		2,
		TIMESTAMP '1991-12-17 00:00:00',
		'Organizzo laboratori per bambini e raccolgo proposte per rendere piu vivi cortili e spazi comuni.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Giorgio',
		'Moretti',
		'giorgio.moretti',
		'giorgio.moretti@rione.test',
		2,
		TIMESTAMP '1974-03-03 00:00:00',
		'Conosco botteghe e officine del Navile, mi piace mettere in contatto chi cerca aiuto pratico.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Ilaria',
		'Serra',
		'ilaria.serra',
		'ilaria.serra@rione.test',
		2,
		TIMESTAMP '1993-06-21 00:00:00',
		'Seguo gruppi di lettura e piccole mostre di quartiere, condivido spesso appuntamenti culturali.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Nicola',
		'Greco',
		'nicola.greco',
		'nicola.greco@rione.test',
		2,
		TIMESTAMP '1982-01-13 00:00:00',
		'Lavoro vicino alla stazione e uso Rione per scambiare avvisi rapidi con chi vive nel Navile.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Sara',
		'Marini',
		'sara.marini',
		'sara.marini@rione.test',
		2,
		TIMESTAMP '1996-09-01 00:00:00',
		'Mi interessano cucina di vicinato, scambio oggetti e segnalazioni gentili per vivere meglio il quartiere.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Davide',
		'Palmieri',
		'davide.palmieri',
		'davide.palmieri@rione.test',
		2,
		TIMESTAMP '1989-04-25 00:00:00',
		'Suono in una sala prove del Navile e segnalo eventi, aperture serali e occasioni musicali locali.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Alessia',
		'Costa',
		'alessia.costa',
		'alessia.costa@rione.test',
		2,
		TIMESTAMP '1978-07-15 00:00:00',
		'Partecipo al comitato del mio palazzo e raccolgo idee su pulizia, sicurezza e cura degli spazi.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Tommaso',
		'Leoni',
		'tommaso.leoni',
		'tommaso.leoni@rione.test',
		2,
		TIMESTAMP '1994-10-29 00:00:00',
		'Mi piace correre al parco e condividere percorsi, ritrovi sportivi e notizie utili per il Navile.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Beatrice',
		'Fabbri',
		'beatrice.fabbri',
		'beatrice.fabbri@rione.test',
		2,
		TIMESTAMP '1986-11-12 00:00:00',
		'Insegno e cerco sempre modi semplici per far circolare materiali, libri e opportunita tra vicini.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Enrico',
		'Villa',
		'enrico.villa',
		'enrico.villa@rione.test',
		2,
		TIMESTAMP '1980-08-18 00:00:00',
		'Aiuto con piccoli traslochi, prestiti di attrezzi e informazioni su artigiani affidabili in zona.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Laura',
		'Pellegrini',
		'laura.pellegrini',
		'laura.pellegrini@rione.test',
		2,
		TIMESTAMP '1997-02-08 00:00:00',
		'Seguo orti urbani e iniziative ambientali, mi piace condividere risorse e appuntamenti sostenibili.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	),
	(
		'Riccardo',
		'Mancini',
		'riccardo.mancini',
		'riccardo.mancini@rione.test',
		2,
		TIMESTAMP '1983-05-26 00:00:00',
		'Vivo vicino al mercato Albani e pubblico spesso avvisi su orari, lavori e occasioni nel quartiere.',
		'pbkdf2$120000$sh79L0/0CxQZXatzM9vV8A==$95e5a132a0845daf98ccff252cf829e0aaba67c85e360962ae83c11c5f55ce3b',
		FALSE
	);
