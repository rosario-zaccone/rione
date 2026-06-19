# User Service Software Requirements Specification
**Version:** v0.4.0  
**Last Updated:** 2026-06-18

## 1. Scope

The User Service manages registered user accounts, authentication-facing profile data, credentials, and neighborhood membership references.

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

### USR-FR-001 - Sign Up

The service shall allow a visitor to create an account with valid account and profile information.

**Acceptance Criteria**
- The service accepts valid sign-up data.
- The service rejects duplicate email addresses and usernames.
- The service stores passwords securely and never exposes them in responses.
- The account can be used for authentication after successful sign up.

### USR-FR-002 - Log In

The service shall allow a registered user to log in with email and password.

**Acceptance Criteria**
- The service authenticates valid credentials.
- The service rejects invalid credentials without revealing which field is incorrect.
- The service denies login for blocked, deleted, or disabled accounts.
- The service returns an authenticated session or token after successful login.

### USR-FR-003 - Update Profile

The service shall allow a registered user to update editable profile information.

**Acceptance Criteria**
- The user can update name, surname, username, neighborhood id, birth date, and bio when valid.
- The service validates required fields, username uniqueness, birth date, and bio length.
- Valid profile changes are persisted.
- Invalid updates are rejected without changing the existing profile.
- When neighborhood id changes, the change is made available to dependent services so social relationships can be reconciled.

### USR-FR-004 - Insert City With Neighborhoods

The service shall allow only an admin user to insert a city with its neighborhoods.

**Acceptance Criteria**
- An admin user can insert a city with one or more neighborhoods.
- A non-admin user cannot insert a city or neighborhoods.
- Unauthorized city insertion attempts are rejected without creating or changing city or neighborhood records.
- Successfully inserted cities and neighborhoods are available for user profile neighborhood selection.

### USR-FR-005 - Remove City With Neighborhoods

The service shall allow only an admin user to remove a city with its neighborhoods.

**Acceptance Criteria**
- An admin user can remove an existing city and its neighborhoods.
- A non-admin user cannot remove a city or neighborhoods.
- Unauthorized city removal attempts are rejected without deleting city or neighborhood records.
- City removal is rejected when any user still belongs to one of the city's neighborhoods.
- Successfully removed cities and neighborhoods are no longer available for user profile neighborhood selection.

## 4. Business Rules

- **USR-BR-001:** A user must have a unique email address and a unique username.
- **USR-BR-002:** A user can belong to one neighborhood at a time.
- **USR-BR-003:** User biography content must contain at least 20 non-whitespace characters so profiles are meaningful while staying quick to scan.
- **USR-BR-004:** A persisted neighborhood change must be available to services that enforce neighborhood-scoped rules.
- **USR-BR-005:** Only an admin user can insert a city and its neighborhoods.
- **USR-BR-006:** Only an admin user can remove a city and its neighborhoods.
- **USR-BR-007:** A city cannot be removed while any user belongs to one of its neighborhoods.

## 5. Non-Functional Requirements

- **USR-NFR-001:** The service must avoid exposing password hashes or sensitive credentials in API responses and logs.
- **USR-NFR-002:** The service must reject invalid input with clear validation errors.
- **USR-NFR-003:** The service must keep identity generation inside persistence adapters.

## 6. Traceability

| Requirement | Source Story | Related Business Rules |
|-------------|--------------|------------------------|
| USR-FR-001 | USR-US-001 | USR-BR-001, USR-BR-003 |
| USR-FR-002 | USR-US-002 | USR-BR-001 |
| USR-FR-003 | USR-US-003 | USR-BR-001, USR-BR-002, USR-BR-003, USR-BR-004 |
| USR-FR-004 | USR-US-004 | USR-BR-005 |
| USR-FR-005 | USR-US-005 | USR-BR-006, USR-BR-007 |
