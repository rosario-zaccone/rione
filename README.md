# Rione

Rione is a didactic microservices project. The current setup is intentionally local-first: tests run with Maven, and local deployment uses Docker Compose.

## Requirements

- Java 21
- Docker and Docker Compose v2
- Maven Wrapper from this repository

## Local Configuration

Create a local `.env` file in the project root:

```env
USER_SERVICE_DB_USERNAME=postgres
USER_SERVICE_DB_PASSWORD=banana
```

The `.env` file is ignored by Git. These credentials are only for local development.

## Test

Run all tests and build checks:

```bash
./mvnw -B verify
```

Run only the user service checks:

```bash
./mvnw -B -pl services/user-service verify
```

## Local Deploy

Start the local user service and PostgreSQL database:

```bash
docker compose up -d --build
```

The local services are:

```text
user-service: http://localhost:8081
postgres: user-postgres:5432 inside Docker Compose
database: rione-users
```

Stop the local environment:

```bash
docker compose down
```

Stop and remove local database data:

```bash
docker compose down -v
```
