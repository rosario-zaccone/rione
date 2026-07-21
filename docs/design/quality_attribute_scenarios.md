# Quality Attribute Scenarios
**Version:** v0.2.0  
**Last Updated:** 2026-07-21

This document defines Quality Attribute Scenarios (QAS) for Rione, following the standard six-part structure: source of stimulus, stimulus, environment, artifact, response, and response measure. Each scenario is paired with the evidence already present in the repository that lets it be checked, not just asserted.

These scenarios were derived after the architecture was already implemented rather than before, as they normally should be. They describe quality attributes the current design already satisfies, and each one points to the concrete mechanism (a test, a configuration, a metric) that makes the claim verifiable.

## Availability

| Field | Description |
|---|---|
| Source of stimulus | A downstream service (e.g. the Social Service) |
| Stimulus | Becomes slow or starts failing repeated calls |
| Environment | Normal operation, api-gateway routing live traffic |
| Artifact | The api-gateway route to the affected service |
| Response | The circuit breaker for that route opens and stops forwarding new calls, so the failure does not cascade to the gateway or to unrelated routes |
| Response measure | The breaker opens once the configured failure-rate/slow-call threshold is reached within its sliding window, and moves to half-open after the configured wait duration |

**Evidence:** Resilience4j circuit breaker configuration in each service's `application.yml`, the `/actuator/health` and `/actuator/circuitbreakers` endpoints, and the circuit breaker wiring in `GatewayRoutesConfiguration`.

## Performance

| Field | Description |
|---|---|
| Source of stimulus | An end user, via the frontend or a direct API client |
| Stimulus | Issues a read request (e.g. fetching the post feed) under normal load |
| Environment | Normal operating conditions, no ongoing incident |
| Artifact | The Post Service API |
| Response | The service returns the requested data |
| Response measure | Average response time stays within an acceptable range for a read endpoint, observed continuously rather than sampled once |

**Evidence:** the "Average Response Time Per Service" panel on the "Rione Microservices" Grafana dashboard, backed by `http_server_requests_seconds` from Micrometer. This measure is average-based rather than percentile-based, since percentile histograms are not currently enabled on `http_server_requests_seconds`; a stricter version of this scenario (e.g. "95% of requests within 300ms") would need that instrumentation added first.

## Modifiability

| Field | Description |
|---|---|
| Source of stimulus | A developer |
| Stimulus | Needs to add or change a domain rule (e.g. a new reaction type in the Post Service) |
| Environment | Design time |
| Artifact | The affected service's `domain`/`application` packages |
| Response | The change stays confined to those packages; `infrastructure` and other services are unaffected |
| Response measure | The service's ArchUnit hexagonal boundary tests still pass after the change, with no new dependency from `domain` to `infrastructure`, Spring, or JPA |

**Evidence:** the `architecture/` test package in every domain service (e.g. `NotificationHexagonalArchitectureTest`), run as part of `./mvnw verify`.

## Testability

| Field | Description |
|---|---|
| Source of stimulus | A developer |
| Stimulus | Changes a business rule in one service |
| Environment | Continuous Integration (GitHub Actions) |
| Artifact | The changed service's test suite and the CI workflow |
| Response | CI runs the full unit/integration/acceptance/architecture suite for the service and fails the build if the change breaks any layer |
| Response measure | `.github/workflows/ci.yml` verifies each service module independently before running the full reactor build, and a broken business rule fails at least one acceptance or unit test |

**Evidence:** the four-way test split (`unit`, `integration`, `acceptance`, `architecture`) mirrored per service, the paired Cucumber feature files under `src/test/resources/features/<service>/`, and the matrix job in `ci.yml`.

## Security

| Field | Description |
|---|---|
| Source of stimulus | An external client without a valid session |
| Stimulus | Sends a request to a protected endpoint with no JWT, an expired JWT, or a JWT belonging to a different user |
| Environment | Normal operation, internet-facing api-gateway |
| Artifact | The target service's `JwtAuthenticationFilter`/`SecurityConfig` |
| Response | The request is rejected before reaching business logic, and no data belonging to another user is returned |
| Response measure | The service responds with 401/403 and an empty or generic error body, verified for every protected endpoint |

**Evidence:** each service's security integration tests (e.g. `NotificationSecurityIntegrationTest`), and the shared `JwtAuthenticationFilter`/`AuthorizationException` building blocks each service carries.

## Scalability

| Field | Description |
|---|---|
| Source of stimulus | An operator |
| Stimulus | Observes a sustained increase in load on one service (e.g. more posting activity on the Post Service) |
| Environment | Deployed system, Docker Compose or an equivalent container runtime |
| Artifact | The affected microservice and its dedicated database |
| Response | That service can be rebuilt, redeployed, or run with more instances without requiring changes to, or downtime of, the other services |
| Response measure | The service builds and deploys independently (its own Dockerfile target and database), and holds no server-side session state that would prevent running multiple instances behind the gateway |

**Evidence:** the "database per service" pattern and independent per-service build/deploy boundary already described in `design_choices.md`, and the stateless JWT-based authentication used by every service (no server-side session).
