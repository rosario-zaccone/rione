# API Gateway User Stories
**Version:** v0.1.0  
**Last Updated:** 2026-06-17

## GW-US-001 - Route Service Requests

**As a** client application  
**I want** to call one public API entry point  
**So that** I can reach Rione service capabilities without knowing internal service locations

**Acceptance Criteria**
- User API requests route to the User Service.
- Social API requests route to the Social Service.
- Post API requests route to the Post Service.
- Notification API requests route to the Notification Service.
- Unknown routes return a consistent not-found response.

## GW-US-002 - Apply Cross-Origin Policy

**As a** browser-based client  
**I want** cross-origin requests to be handled consistently  
**So that** the frontend can call the API safely from allowed origins

**Acceptance Criteria**
- Allowed origins can access configured public routes.
- Disallowed origins are rejected by policy.
- Preflight requests receive the configured allowed methods and headers.

## Business Rules

- **GW-BR-001:** The gateway must not implement domain business rules owned by backend services.
- **GW-BR-002:** The gateway must route requests without exposing internal service topology beyond configured public APIs.
