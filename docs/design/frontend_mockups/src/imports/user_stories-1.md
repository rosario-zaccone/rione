# User Service User Stories
**Version:** v0.5.0  
**Last Updated:** 2026-06-23

## USR-US-001 - Sign Up

**As a** visitor  
**I want** to create an account  
**So that** I can access the platform as a registered user

**Acceptance Criteria**
- A visitor can sign up by providing valid account and profile information.
- The platform rejects duplicate email addresses and duplicate usernames.
- The platform stores the password securely and never exposes it in responses.
- After successful sign up, the user account is available for authentication.

## USR-US-002 - Log In

**As a** registered user  
**I want** to log in with my email and password  
**So that** I can securely access my account

**Acceptance Criteria**
- A registered user can log in with a valid email and password.
- The platform rejects invalid credentials without revealing which field is incorrect.
- The platform denies login for accounts that are blocked, deleted, or otherwise disabled.
- A successful login provides the user with an authenticated session or token.

## USR-US-003 - Update Profile

**As a** registered user  
**I want** to update my profile info  
**So that** my neighbours can recognize me and see accurate information about me

**Acceptance Criteria**
- A registered user can update their editable profile information.
- The platform validates required fields, username uniqueness, birth date, and bio length.
- Profile changes are persisted and available to other services.
- Invalid profile updates are rejected without changing the existing profile.
- When the user changes neighborhood, the platform makes the change available so neighborhood-scoped relationships can be reconciled.

## USR-US-004 - Insert City With Neighborhoods

**As an** admin user  
**I want** to insert a city with its neighborhoods  
**So that** users can select valid neighborhood membership from platform-managed locations

**Acceptance Criteria**
- An admin user can insert a city with one or more neighborhoods.
- A non-admin user cannot insert a city or neighborhoods.
- The platform rejects city insertion when the acting user is not an admin.
- The inserted city and neighborhoods are available for user profile neighborhood selection.

## USR-US-005 - Remove City With Neighborhoods

**As an** admin user  
**I want** to remove a city with its neighborhoods  
**So that** obsolete platform-managed locations are no longer available for user profile neighborhood selection

**Acceptance Criteria**
- An admin user can remove an existing city and its neighborhoods.
- A non-admin user cannot remove a city or neighborhoods.
- The platform rejects city removal when the acting user is not an admin.
- The platform rejects city removal when any user still belongs to one of the city's neighborhoods.
- Removed cities and neighborhoods are no longer available for user profile neighborhood selection.

## USR-US-006 - Log Out

**As a** registered user  
**I want** to log out  
**So that** my current access token can no longer be used to access my account

**Acceptance Criteria**
- An authenticated user can log out using their current access token.
- The platform invalidates the current access token.
- Requests made with the invalidated token are rejected as unauthorized.
- Other access tokens issued to the same user are not invalidated.

## Business Rules

- **USR-BR-001:** A user must have a unique email address and a unique username.
- **USR-BR-002:** A user can belong to one neighborhood at a time.
- **USR-BR-003:** User biography content must contain at least 20 non-whitespace characters.
- **USR-BR-004:** A persisted neighborhood change must be available to services that enforce neighborhood-scoped rules.
- **USR-BR-005:** Only an admin user can insert a city and its neighborhoods.
- **USR-BR-006:** Only an admin user can remove a city and its neighborhoods.
- **USR-BR-007:** A city cannot be removed while any user belongs to one of its neighborhoods.
