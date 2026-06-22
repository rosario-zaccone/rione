# Rione

Rione is a didactic microservices project. The local setup runs the backend services with Docker Compose and exposes the public API through the API Gateway.

## Requirements

- Java 21
- Docker and Docker Compose v2
- Maven Wrapper from this repository

## Local Configuration

You can run the project with the default local credentials, or create a `.env` file in the project root to override them:

```env
USER_SERVICE_DB_USERNAME=postgres
USER_SERVICE_DB_PASSWORD=password
SOCIAL_SERVICE_DB_USERNAME=postgres
SOCIAL_SERVICE_DB_PASSWORD=password
PGADMIN_DEFAULT_EMAIL=admin@example.com
PGADMIN_DEFAULT_PASSWORD=password
```

The `.env` file is ignored by Git and is intended only for local development.

## Local Seed Data

The local user dataset is documented in `state.md`, which is ignored by Git so it can stay in sync with local manual tests without polluting the repository history.

## Start the Backend

From the project root, build and start all backend containers:

```bash
docker compose up --build
```

Run in detached mode if you want to keep using the same terminal:

```bash
docker compose up -d --build
```

Main local URLs:

```text
API Gateway:    http://localhost:8080
User Service:   http://localhost:8081
Social Service: http://localhost:8082
pgAdmin:        http://localhost:5050
```

Stop the backend:

```bash
docker compose down
```

Stop the backend and remove local database volumes:

```bash
docker compose down -v
```

## Swagger / OpenAPI

Open the API Gateway Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

The Swagger UI contains the available public API groups:

```text
user-service
social-service
```

Raw OpenAPI documents are also available through the gateway:

```text
http://localhost:8080/users/v3/api-docs
http://localhost:8080/social/v3/api-docs
```

The static OpenAPI contract files are stored in:

```text
docs/openapi/
```

## Health Checks

The backend services expose Spring Boot Actuator health endpoints.

```text
API Gateway:    http://localhost:8080/actuator/health
User Service:   http://localhost:8081/actuator/health
Social Service: http://localhost:8082/actuator/health
```

Example:

```bash
curl http://localhost:8080/actuator/health
```

Expected response when the service is running:

```json
{
  "status": "UP"
}
```

The gateway health endpoint reports the gateway status. To check the whole local backend, call the health endpoint of each service.

## Run Tests

Run all tests and build checks:

```bash
./mvnw -B verify
```

Run only the social service checks:

```bash
./mvnw -B -pl services/social-service -am test
```

Run only the user service checks:

```bash
./mvnw -B -pl services/user-service -am test
```
