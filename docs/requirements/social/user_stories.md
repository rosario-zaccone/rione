# Social Service User Stories
**Version:** v0.3.2  
**Last Updated:** 2026-06-20

## SOC-US-001 - Search Neighbours

**As a** registered user  
**I want** to search for neighbours and see their profile information  
**So that** I can find people in my neighbourhood and decide who to connect with

**Acceptance Criteria**
- A registered user can search users in their own neighborhood by supported profile fields.
- Search results exclude blocked users and users who should not be visible to the requester.
- Search results show only profile information allowed by privacy rules.
- Empty searches or no-match searches return an empty result set instead of an error.

## SOC-US-002 - Send Neighbour Request

**As a** registered user  
**I want** to send a neighbour request to another user  
**So that** we can become connected once they accept

**Acceptance Criteria**
- A registered user can send a neighbour request to a visible user in the same neighborhood.
- The platform prevents duplicate pending requests between the same users.
- The platform prevents users from sending requests to themselves or blocked users.
- The recipient receives a notification when the request is created.

## SOC-US-003 - Respond to Neighbour Request

**As a** registered user  
**I want** to accept or decline a neighbour request  
**So that** I can decide whether to connect with another user

**Acceptance Criteria**
- A recipient can accept or decline a pending neighbour request addressed to them.
- Accepting a request creates a neighbour connection between both users.
- Declining a request closes it without creating a connection.
- The platform rejects responses to expired, already handled, or unauthorized requests.

## SOC-US-004 - Remove Neighbour

**As a** registered user  
**I want** to remove a neighbour from my contacts  
**So that** I can keep my neighbourhood contacts relevant and up to date

**Acceptance Criteria**
- A registered user can remove an existing neighbour connection.
- Removing a neighbour deletes the connection for both users.
- Removing a neighbour does not delete historical posts, comments, or reactions.
- The platform handles attempts to remove a non-existing connection without creating duplicate side effects.

## SOC-US-005 - Block Neighbour

**As a** registered user  
**I want** to block a neighbour  
**So that** they can no longer contact me or see my private activity

**Acceptance Criteria**
- A registered user can block another user who is visible to them.
- Blocking a user removes any existing neighbour connection and pending requests between the two users.
- A blocked user cannot send neighbour requests, comments, reactions, or direct interactions to the blocker.
- Blocking does not remove already published public or moderation-required records.

## SOC-US-006 - Unblock User

**As a** registered user  
**I want** to unblock a user I previously blocked  
**So that** future interactions can follow the normal platform rules again

**Acceptance Criteria**
- A registered user can unblock a user they previously blocked.
- The unblocked user is no longer prevented by that block from eligible future interactions.
- Unblocking does not recreate deleted neighbour connections or pending requests.
- The platform handles attempts to unblock a user who is not currently blocked without duplicate side effects.

## SOC-US-007 - List Blocked Users

**As a** registered user  
**I want** to view the users I have blocked  
**So that** I can review and manage my blocked users

**Acceptance Criteria**
- A registered user can retrieve the blocks they created.
- The response identifies each blocked user.
- A user with no blocked users receives an empty list.
- Blocks created by other users are not included.

## SOC-US-008 - Reconcile Relationships After Neighborhood Change

**As a** registered user  
**I want** my neighbour connections to match my current neighborhood  
**So that** my social graph only contains eligible neighbours

**Acceptance Criteria**
- When a user changes neighborhood, the platform removes neighbour connections with users outside the new neighborhood.
- The platform removes pending neighbour requests with users outside the new neighborhood.
- The platform preserves neighbour connections and pending requests with users who still belong to the same neighborhood.
- The platform preserves historical posts, comments, reactions, and notifications.

## SOC-US-009 - View Sent And Received Neighbour Requests

**As a** registered user  
**I want** to view neighbour requests I sent and neighbour requests I received  
**So that** I can track pending connection workflows separately

**Acceptance Criteria**
- A registered user can retrieve neighbour requests they sent.
- A registered user can retrieve neighbour requests they received.
- Sent and received neighbour requests are shown in separate lists.
- Neighbour requests involving other users are not included.

## Business Rules

- **SOC-BR-001:** Neighbour requests can only be sent between users in the same neighborhood.
- **SOC-BR-002:** A user cannot send a neighbour request to themselves.
- **SOC-BR-003:** Only one pending neighbour request can exist between the same two users at the same time.
- **SOC-BR-004:** Accepting a neighbour request creates a reciprocal neighbour connection.
- **SOC-BR-005:** Declining, removing, or blocking a neighbour does not delete historical posts, comments, reactions, or notifications.
- **SOC-BR-006:** Blocking takes precedence over neighbour connections, search visibility, requests, reactions, comments, and notifications.
- **SOC-BR-007:** Unblocking removes the block but does not restore previous neighbour connections or pending requests.
- **SOC-BR-008:** When a user changes neighborhood, the platform removes neighbour connections and pending neighbour requests with users who no longer belong to the same neighborhood.
- **SOC-BR-009:** A user can only list blocks they created.
- **SOC-BR-010:** A user can only list neighbour requests they sent or received.
