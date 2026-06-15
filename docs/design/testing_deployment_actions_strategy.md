# Testing, Deployment, and Actions Strategy
**Version:** v0.2.0  
**Last Updated:** 2026-06-15

This document describes how Rione verifies code and runs local deployment for a simple didactic development workflow.

## Goals

- Keep the project easy to run on a developer machine.
- Verify every microservice with Maven.
- Use Docker Compose only for local deployment.
- Avoid remote deployment, SSH, registry publishing, staging, and production complexity.

## Branching Strategy

For the university project scope, Rione uses a simple Git workflow:

```text
feature/* -> main
```

- `feature/*` branches are used for active development.
- Pull requests target `main` when review is useful.
- Version tags are optional and do not trigger deployment.
- There are no staging or production environments.

## Testing Strategy

Testing is handled by `.github/workflows/ci.yml`.

CI runs on:

- Pushes to `feature/*`
- Pushes to `main`
- Pull requests targeting `main`
- Manual workflow dispatch

The workflow uses Java 21, the Maven Wrapper, and Maven dependency caching. Each microservice is verified explicitly:

```text
services/user-service
services/post-service
services/social-service
services/notification-service
```

For each module, CI runs:

```bash
./mvnw -B -pl services/<service-name> verify
```

After module verification passes, CI runs the full Maven reactor:

```bash
./mvnw -B verify
```

Unit tests use JUnit and are expected to follow the `*Test` naming convention. Integration tests are expected to follow the `*IT` naming convention and run during Maven `verify`.

The user service separates tests by source directory:

```text
src/test/java/unit
src/test/java/integration
src/test/java/architecture
```

Architecture tests use ArchUnit to enforce hexagonal and clean architecture dependency rules.

## GitHub Actions Strategy

The repository keeps a single workflow file:

```text
.github/workflows/ci.yml
```

`ci.yml` is responsible only for tests, verification, and Maven builds. It must not publish Docker images, connect over SSH, or deploy environments.

There is no CD workflow because the project only supports local deployment.

## Docker Strategy

Docker is used locally through:

```text
docker-compose.yml
```

The local Compose file builds `user-service` from the shared root `Dockerfile` by passing:

```text
SERVICE_PATH=services/user-service
```

It also starts a dedicated PostgreSQL container for the user service.

## Local Deployment Strategy

Local deployment uses Docker Compose v2:

```bash
docker compose up -d --build
```

The local services are:

```text
user-service: http://localhost:8081
user-postgres: user-postgres:5432 inside Docker Compose
database: rione-users
```

Stop the local environment:

```bash
docker compose down
```

Stop the local environment and remove database data:

```bash
docker compose down -v
```

## Environment Strategy

Local database credentials are read from `.env`:

```env
USER_SERVICE_DB_USERNAME=postgres
USER_SERVICE_DB_PASSWORD=banana
```

The `.env` file is ignored by Git. These values are acceptable for local didactic development only and must not be used for real public deployments.
