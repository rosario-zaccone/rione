# User Service Software Requirements Specification
**Version:** v0.6.1  
**Last Modified:** 2026-07-09

## 1. Scope

The User Service manages user accounts, profile data, credentials, and neighborhood membership references.

## 2. Definitions

- **User:** A registered person who uses the platform.
- **Visitor:** A person who has not authenticated and can create an account.
- **Registered user:** An authenticated person who can use platform features.
- **Admin user:** An authenticated user with administrative privileges.
- **Profile:** User-facing account information such as full name, username, birth date, biography, and neighborhood id.
- **City:** A platform-managed location containing one or more neighborhoods.
- **Neighborhood:** A platform-managed area that belongs to a city and can be referenced by a user profile.
- **Neighborhood id:** The identifier of the neighborhood the user belongs to.

## 3. Functional Requirements

| Code | Short Description | Origin |
|------|-------------------|--------|
| USR-FR-001 | Allow a visitor to create an account with valid account and profile information. | USR-US-001 |
| USR-FR-002 | Allow a registered user to log in with email and password. | USR-US-002 |
| USR-FR-003 | Allow a registered user to update editable profile information and make neighborhood changes available to dependent services. | USR-US-003 |
| USR-FR-004 | Allow only an admin user to insert a city with one or more neighborhoods. | USR-US-004 |
| USR-FR-005 | Allow only an admin user to remove a city with its neighborhoods when no user belongs to those neighborhoods. | USR-US-005 |
| USR-FR-006 | Allow an authenticated user to log out by invalidating the access token used for the request. | USR-US-006 |

## 4. Business Rules

| Code | Short Description | Origin |
|------|-------------------|--------|
| USR-BR-001 | A user must have a unique email address and a unique username. | USR-US-001, USR-US-002, USR-US-003 |
| USR-BR-002 | A user can belong to one neighborhood at a time. | USR-US-003 |
| USR-BR-003 | User biography content must contain at least 20 non-whitespace characters so profiles are meaningful while staying quick to scan. | USR-US-001, USR-US-003 |
| USR-BR-004 | A persisted neighborhood change must be available to services that enforce neighborhood-scoped rules. | USR-US-003 |
| USR-BR-005 | Only an admin user can insert a city and its neighborhoods. | USR-US-004 |
| USR-BR-006 | Only an admin user can remove a city and its neighborhoods. | USR-US-005 |
| USR-BR-007 | A city cannot be removed while any user belongs to one of its neighborhoods. | USR-US-005 |

## 5. Non-Functional Requirements

- **USR-NFR-001:** The service must avoid exposing password hashes or sensitive credentials in API responses and logs.
- **USR-NFR-002:** The service must reject invalid input with clear validation errors.
- **USR-NFR-003:** The service must keep identity generation inside persistence adapters.
