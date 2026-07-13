# API Gateway Software Requirements Specification
**Version:** v0.2.1  
**Last Modified:** 2026-07-09

## 1. Scope

The API Gateway is the public HTTP entry point for Rione. It receives client requests and routes them to the right service API.

## 2. Definitions

- **Gateway route:** A public route that forwards traffic to a backend service.
- **Backend service:** A Rione service behind the gateway.
- **Cross-origin request:** A browser request controlled by CORS policy.

## 3. Functional Requirements

| Code | Short Description | Origin |
|------|-------------------|--------|
| GW-FR-001 | Route public API requests to the correct backend service, including user, social, post, and notification APIs. | GW-US-001 |
| GW-FR-002 | Apply the configured CORS policy to public API traffic, including preflight requests. | GW-US-002 |

## 4. Business Rules

| Code | Short Description | Origin |
|------|-------------------|--------|
| GW-BR-001 | The gateway must not implement domain business rules owned by backend services. | GW-US-001 |
| GW-BR-002 | The gateway must route requests without exposing internal service topology beyond configured public APIs. | GW-US-001, GW-US-002 |

## 5. Non-Functional Requirements

- **GW-NFR-001:** Gateway routing should be deterministic and observable.
- **GW-NFR-002:** Gateway configuration should be environment-driven where deployment requires different service endpoints.
