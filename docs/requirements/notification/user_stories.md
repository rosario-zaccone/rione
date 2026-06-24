# Notification Service User Stories
**Version:** v0.2.0  
**Last Updated:** 2026-06-24

## NOT-US-001 - Receive Request Received Notifications

**As a** registered user  
**I want** to receive a notification when another user sends me a neighbour request  
**So that** I can notice and respond to incoming neighbour requests

**Acceptance Criteria**
- A registered user receives a `REQUEST_RECEIVED` notification when another user sends them a neighbour request.
- The platform does not send notifications for interactions hidden by blocking or visibility rules.
- Notification delivery failures do not roll back the original user action that triggered the notification.

## NOT-US-002 - Receive Request Accepted Notifications

**As a** registered user  
**I want** to receive a notification when another user accepts my neighbour request  
**So that** I know the neighbour connection has been created

**Acceptance Criteria**
- A registered user receives a `REQUEST_ACCEPTED` notification when another user accepts their neighbour request.
- The platform does not send notifications for interactions hidden by blocking or visibility rules.
- Notification delivery failures do not roll back the original user action that triggered the notification.

## NOT-US-003 - Mark Notifications As Read

**As a** registered user  
**I want** to mark a notification as read  
**So that** I can keep track of which notifications still need my attention

**Acceptance Criteria**
- A registered user can mark one of their notifications as read.
- Marking a notification as read records the read state for that notification.
- A user cannot mark another user's notification as read.
- Repeating the mark-as-read action on an already read notification keeps the notification read.

## Business Rules

- **NOT-BR-001:** A request received notification is generated for the receiver when a neighbour request is sent.
- **NOT-BR-002:** Notification delivery must not block or roll back the original action.
- **NOT-BR-003:** Notifications must not be generated for interactions hidden by blocking or visibility rules.
- **NOT-BR-004:** A request accepted notification is generated for the original sender when a neighbour request is accepted.
- **NOT-BR-005:** Only the notification recipient can mark the notification as read.
