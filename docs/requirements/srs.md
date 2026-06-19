# Software Requirements Specifications
**Version:** v0.4.0  
**Last Updated:** 2026-06-18

This index points to service-specific Software Requirements Specifications for Rione and records platform-level requirements that span service boundaries.

## Service Documents

- [API Gateway SRS](api-gateway/srs.md)
- [User Service SRS](user/srs.md)
- [Social Service SRS](social/srs.md)
- [Post Service SRS](post/srs.md)
- [Notification Service SRS](notification/srs.md)

## Scope

Each service SRS defines functional requirements, acceptance criteria, business rules, non-functional requirements, and traceability for that service boundary.

## Platform Requirements

### PLT-FR-001 - Insert City With Neighborhoods

The platform shall allow only an admin user to insert a city with its neighborhoods.

**Acceptance Criteria**
- An admin user can insert a city with one or more neighborhoods.
- A non-admin user cannot insert a city or neighborhoods.
- Unauthorized city insertion attempts are rejected without creating or changing city or neighborhood records.
- Successfully inserted cities and neighborhoods are available for user profile neighborhood selection.

### PLT-FR-002 - Remove City With Neighborhoods

The platform shall allow only an admin user to remove a city with its neighborhoods.

**Acceptance Criteria**
- An admin user can remove an existing city and its neighborhoods.
- A non-admin user cannot remove a city or neighborhoods.
- Unauthorized city removal attempts are rejected without deleting city or neighborhood records.
- City removal is rejected when any user still belongs to one of the city's neighborhoods.
- Successfully removed cities and neighborhoods are no longer available for user profile neighborhood selection.

## Platform Business Rules

- **PLT-BR-001:** Only an admin user can insert a city and its neighborhoods.
- **PLT-BR-002:** Only an admin user can remove a city and its neighborhoods.
- **PLT-BR-003:** A city cannot be removed while any user belongs to one of its neighborhoods.

## Platform Traceability

| Requirement | Source Story | Related Business Rules |
|-------------|--------------|------------------------|
| PLT-FR-001 | PLT-US-001 | PLT-BR-001 |
| PLT-FR-002 | PLT-US-002 | PLT-BR-002, PLT-BR-003 |
