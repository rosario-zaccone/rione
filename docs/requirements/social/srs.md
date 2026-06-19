# Social Service Software Requirements Specification
**Version:** v0.3.0  
**Last Updated:** 2026-06-17

## 1. Scope

The Social Service manages neighbour discovery, neighbour requests, neighbour connections, and blocking.

## 2. Definitions

- **Neighbour:** A user who belongs to the same neighborhood as another user.
- **Neighbour request:** A request sent by one user to another user to establish a neighbour connection.
- **Neighborship:** An accepted neighbour connection.
- **Blocked user:** A user that another user has chosen to prevent from interacting with them.
- **Unblock:** The action of removing an existing block between two users.

## 3. Functional Requirements

### SOC-FR-001 - Search Neighbours

The service shall allow a registered user to search for neighbours in their neighborhood.

**Acceptance Criteria**
- Search can use supported profile fields.
- Results exclude blocked users and users hidden by visibility rules.
- Results show only allowed profile information.
- No-match searches return an empty result set.

### SOC-FR-002 - Send Neighbour Request

The service shall allow a registered user to send a neighbour request to another eligible user.

**Acceptance Criteria**
- The requester can send a request to a visible user in the same neighborhood.
- Duplicate pending requests are rejected.
- Requests to self or blocked users are rejected.
- The recipient receives a notification when the request is created.

### SOC-FR-003 - Respond to Neighbour Request

The service shall allow a recipient to accept or decline a pending neighbour request.

**Acceptance Criteria**
- Accepting a request creates a neighbour connection.
- Declining a request closes it without creating a connection.
- Only the recipient can respond to the request.
- Expired or already handled requests are rejected.

### SOC-FR-004 - Remove Neighbour

The service shall allow a registered user to remove an existing neighbour connection.

**Acceptance Criteria**
- Removing a neighbour deletes the connection for both users.
- Historical posts, comments, and reactions are preserved.
- Removing a non-existing connection creates no duplicate side effects.

### SOC-FR-005 - Block User

The service shall allow a registered user to block another user.

**Acceptance Criteria**
- Blocking removes existing neighbour connections and pending requests between the users.
- The blocked user cannot send requests, comments, reactions, or direct interactions to the blocker.
- Blocking does not delete already published public or moderation-required records.

### SOC-FR-006 - Unblock User

The service shall allow a registered user to unblock a user they previously blocked.

**Acceptance Criteria**
- The blocker can unblock a user they previously blocked.
- Unblocking removes the block and allows future eligible interactions according to normal visibility and relationship rules.
- Unblocking does not recreate previous neighbour connections or pending requests.
- Unblocking a user who is not currently blocked creates no duplicate side effects.

### SOC-FR-007 - Reconcile Relationships After Neighborhood Change

The service shall reconcile social relationships when a user changes neighborhood.

**Acceptance Criteria**
- When a user's neighborhood changes, neighbour connections with users outside the new neighborhood are removed.
- Pending neighbour requests with users outside the new neighborhood are removed.
- Relationships with users who remain in the same neighborhood are preserved.
- Historical posts, comments, reactions, and notifications are preserved.

## 4. Business Rules

- **SOC-BR-001:** Neighbour requests can only be sent between users in the same neighborhood.
- **SOC-BR-002:** A user cannot send a neighbour request to themselves.
- **SOC-BR-003:** Only one pending neighbour request can exist between the same two users at the same time.
- **SOC-BR-004:** Accepting a neighbour request creates a reciprocal neighbour connection.
- **SOC-BR-005:** Declining, removing, or blocking a neighbour does not delete historical posts, comments, reactions, or notifications.
- **SOC-BR-006:** Blocking takes precedence over neighbour connections, search visibility, requests, reactions, comments, and notifications.
- **SOC-BR-007:** Unblocking removes the block but does not restore previous neighbour connections or pending requests.
- **SOC-BR-008:** When a user changes neighborhood, the platform removes neighbour connections and pending neighbour requests with users who no longer belong to the same neighborhood.

## 5. Non-Functional Requirements

- **SOC-NFR-001:** The service must enforce blocking before social interactions.
- **SOC-NFR-002:** The service must preserve historical relationship decisions for audit and consistency where required.
- **SOC-NFR-003:** The service must reject invalid social operations with clear errors.

## 6. Traceability

| Requirement | Source Story | Related Business Rules |
|-------------|--------------|------------------------|
| SOC-FR-001 | SOC-US-001 | SOC-BR-006 |
| SOC-FR-002 | SOC-US-002 | SOC-BR-001, SOC-BR-002, SOC-BR-003, SOC-BR-006 |
| SOC-FR-003 | SOC-US-003 | SOC-BR-004 |
| SOC-FR-004 | SOC-US-004 | SOC-BR-005 |
| SOC-FR-005 | SOC-US-005 | SOC-BR-005, SOC-BR-006 |
| SOC-FR-006 | SOC-US-006 | SOC-BR-006, SOC-BR-007 |
| SOC-FR-007 | SOC-US-007 | SOC-BR-001, SOC-BR-005, SOC-BR-008 |
