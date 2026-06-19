# API Gateway Software Requirements Specification
**Version:** v0.1.0  
**Last Updated:** 2026-06-17

## 1. Scope

The API Gateway provides the public HTTP entry point for Rione services and routes requests to service APIs.

## 2. Definitions

- **Gateway route:** A public route that forwards traffic to a backend service.
- **Backend service:** A Rione service behind the gateway.
- **Cross-origin request:** A browser request controlled by CORS policy.

## 3. Functional Requirements

### GW-FR-001 - Route Service Requests

The gateway shall route public API requests to the appropriate backend service.

**Acceptance Criteria**
- User API requests route to the User Service.
- Social API requests route to the Social Service.
- Post API requests route to the Post Service.
- Notification API requests route to the Notification Service.
- Unknown routes return a consistent not-found response.

### GW-FR-002 - Apply Cross-Origin Policy

The gateway shall apply the configured CORS policy to public API traffic.

**Acceptance Criteria**
- Allowed origins can access configured public routes.
- Disallowed origins are rejected by policy.
- Preflight requests receive the configured allowed methods and headers.

## 4. Business Rules

- **GW-BR-001:** The gateway must not implement domain business rules owned by backend services.
- **GW-BR-002:** The gateway must route requests without exposing internal service topology beyond configured public APIs.

## 5. Non-Functional Requirements

- **GW-NFR-001:** Gateway routing should be deterministic and observable.
- **GW-NFR-002:** Gateway configuration should be environment-driven where deployment requires different service endpoints.

## 6. Traceability

| Requirement | Source Story | Related Business Rules |
|-------------|--------------|------------------------|
| GW-FR-001 | GW-US-001 | GW-BR-001, GW-BR-002 |
| GW-FR-002 | GW-US-002 | GW-BR-002 |
