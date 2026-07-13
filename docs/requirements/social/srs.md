# Social Service Software Requirements Specification
**Version:** v0.5.1  
**Last Modified:** 2026-07-09

## 1. Scope

The Social Service manages neighbour discovery, neighbour requests, neighbour connections, and blocking.

## 2. Definitions

- **Neighbour:** A user who belongs to the same neighborhood as another user.
- **Neighbour request:** A request sent by one user to another user to establish a neighbour connection.
- **Neighborship:** An accepted neighbour connection.
- **Blocked user:** A user that another user has chosen to prevent from interacting with them.
- **Unblock:** The action of removing an existing block between two users.

## 3. Functional Requirements

| Code | Short Description | Origin |
|------|-------------------|--------|
| SOC-FR-001 | Allow a registered user to search visible neighbours in their neighborhood by username, name, or surname. | SOC-US-001 |
| SOC-FR-002 | Allow a registered user to send a neighbour request to another eligible user in the same neighborhood. | SOC-US-002 |
| SOC-FR-003 | Allow a request recipient to accept or decline a pending neighbour request. | SOC-US-003 |
| SOC-FR-004 | Allow a registered user to remove an existing neighbour connection without deleting historical activity. | SOC-US-004 |
| SOC-FR-005 | Allow a registered user to block another user and prevent direct social interactions. | SOC-US-005 |
| SOC-FR-006 | Allow a registered user to unblock a previously blocked user without restoring prior relationships. | SOC-US-006 |
| SOC-FR-007 | Allow a registered user to list the users they have blocked. | SOC-US-007 |
| SOC-FR-008 | Reconcile neighbour connections and pending requests after a user changes neighborhood. | SOC-US-008 |
| SOC-FR-009 | Allow a registered user to list neighbour requests they sent and received. | SOC-US-009 |

## 4. Business Rules

| Code | Short Description | Origin |
|------|-------------------|--------|
| SOC-BR-001 | Neighbour requests can only be sent between users in the same neighborhood. | SOC-US-002, SOC-US-008 |
| SOC-BR-002 | A user cannot send a neighbour request to themselves. | SOC-US-002 |
| SOC-BR-003 | Only one pending neighbour request can exist between the same two users at the same time. | SOC-US-002 |
| SOC-BR-004 | Accepting a neighbour request creates a reciprocal neighbour connection. | SOC-US-003 |
| SOC-BR-005 | Declining, removing, or blocking a neighbour does not delete historical posts, comments, reactions, or notifications. | SOC-US-004, SOC-US-005, SOC-US-008 |
| SOC-BR-006 | Blocking takes precedence over neighbour connections, search visibility, requests, reactions, comments, and notifications. | SOC-US-001, SOC-US-002, SOC-US-005, SOC-US-006 |
| SOC-BR-007 | Unblocking removes the block but does not restore previous neighbour connections or pending requests. | SOC-US-006 |
| SOC-BR-008 | When a user changes neighborhood, the platform removes neighbour connections and pending neighbour requests with users who no longer belong to the same neighborhood. | SOC-US-008 |
| SOC-BR-009 | A user can only list blocks they created. | SOC-US-007 |
| SOC-BR-010 | A user can only list neighbour requests they sent or received. | SOC-US-009 |

## 5. Non-Functional Requirements

- **SOC-NFR-001:** The service must enforce blocking before social interactions.
- **SOC-NFR-002:** The service must preserve historical relationship decisions for audit and consistency where required.
- **SOC-NFR-003:** The service must reject invalid social operations with clear errors.
