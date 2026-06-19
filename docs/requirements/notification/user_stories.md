# Notification Service User Stories
**Version:** v0.1.0  
**Last Updated:** 2026-06-17

## NOT-US-001 - Receive Notifications

**As a** registered user  
**I want** to receive notifications  
**So that** I can stay updated about reactions and comments on my posts, neighbour requests, and moderation information

**Acceptance Criteria**
- A registered user receives notifications for relevant neighbour requests, post interactions, and moderation events.
- The platform does not send notifications for interactions hidden by blocking or visibility rules.
- A user can view unread and read notification states.
- Notification delivery failures do not roll back the original user action that triggered the notification.

## Business Rules

- **NOT-BR-001:** Notifications are generated for relevant user actions.
- **NOT-BR-002:** Notification delivery must not block or roll back the original action.
- **NOT-BR-003:** Notifications must not be generated for interactions hidden by blocking or visibility rules.
