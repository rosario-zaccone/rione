# User Stories
**Version:** v0.6.0  
**Last Updated:** 2026-06-18

This index points to service-specific user story documents for Rione and records platform-level user stories that span service boundaries.

## Service Documents

- [API Gateway User Stories](api-gateway/user_stories.md)
- [User Service User Stories](user/user_stories.md)
- [Social Service User Stories](social/user_stories.md)
- [Post Service User Stories](post/user_stories.md)
- [Notification Service User Stories](notification/user_stories.md)

## Platform User Stories

### PLT-US-001 - Insert City With Neighborhoods

**As an** admin user  
**I want** to insert a city with its neighborhoods  
**So that** users can select valid neighborhood membership from platform-managed locations

**Acceptance Criteria**
- An admin user can insert a city with one or more neighborhoods.
- A non-admin user cannot insert a city or neighborhoods.
- The platform rejects city insertion when the acting user is not an admin.
- The inserted city and neighborhoods are available for user profile neighborhood selection.

### PLT-US-002 - Remove City With Neighborhoods

**As an** admin user  
**I want** to remove a city with its neighborhoods  
**So that** obsolete platform-managed locations are no longer available for user profile neighborhood selection

**Acceptance Criteria**
- An admin user can remove an existing city and its neighborhoods.
- A non-admin user cannot remove a city or neighborhoods.
- The platform rejects city removal when the acting user is not an admin.
- The platform rejects city removal when any user still belongs to one of the city's neighborhoods.
- Removed cities and neighborhoods are no longer available for user profile neighborhood selection.

## Story Template

**As a** [type of user]  
**I want** [goal or capability]  
**So that** [benefit or reason]

**Acceptance Criteria**
- [Expected observable behavior]
- [Relevant validation or rule]
- [Expected error or edge-case behavior]
