# Notification Service User Stories
**Version:** v0.3.1  
**Last Modified:** 2026-07-09

## NOT-US-001 - Receive Request Received Notifications

**As a** registered user  
**I want** to receive a notification when another user sends me a neighbour request  
**So that** I can notice the request and respond to it

**Acceptance Criteria**
- A registered user receives a `REQUEST_RECEIVED` notification when another user sends them a neighbour request.
- The platform does not send notifications for interactions hidden by blocking or visibility rules.
- Notification delivery failures do not roll back the original user action that triggered the notification.

## NOT-US-002 - Receive Request Accepted Notifications

**As a** registered user  
**I want** to receive a notification when another user accepts my neighbour request  
**So that** I know the neighbour connection is active

**Acceptance Criteria**
- A registered user receives a `REQUEST_ACCEPTED` notification when another user accepts their neighbour request.
- The platform does not send notifications for interactions hidden by blocking or visibility rules.
- Notification delivery failures do not roll back the original user action that triggered the notification.

## NOT-US-003 - Mark Notifications As Read

**As a** registered user  
**I want** to mark a notification as read  
**So that** I can see which notifications still need my attention

**Acceptance Criteria**
- A registered user can mark one of their notifications as read.
- Marking a notification as read records the read state for that notification.
- A user cannot mark another user's notification as read.
- Repeating the mark-as-read action on an already read notification keeps the notification read.

## NOT-US-004 - Receive Post Comment Notifications

**As a** post author  
**I want** to receive a notification when another eligible user comments on my post  
**So that** I can follow conversations around content I shared

**Acceptance Criteria**
- A post author receives a `POST_COMMENT_ADDED` notification when another eligible user comments on their post.
- The notification identifies the actor who added the comment and the post that received the comment.
- The platform does not send a post comment notification when the post author comments on their own post.
- The platform does not send notifications for comments hidden by blocking or visibility rules.
- Notification delivery failures do not roll back the original comment action.

## NOT-US-005 - Receive Post Reaction Notifications

**As a** post author  
**I want** to receive a notification when another eligible user reacts to my post  
**So that** I know when someone in the neighbourhood reacts to my post

**Acceptance Criteria**
- A post author receives a `POST_REACTION_ADDED` notification when another eligible user adds a reaction to their post.
- The notification identifies the actor who reacted and the post that received the reaction.
- The platform does not send a post reaction notification when the post author reacts to their own post.
- Updating or removing an existing reaction does not create a new reaction-added notification.
- The platform does not send notifications for reactions hidden by blocking or visibility rules.
- Notification delivery failures do not roll back the original reaction action.

## Business Rules

- **NOT-BR-001:** A request received notification is generated for the receiver when a neighbour request is sent.
- **NOT-BR-002:** Notification delivery must not block or roll back the original action.
- **NOT-BR-003:** Notifications must not be generated for interactions hidden by blocking or visibility rules.
- **NOT-BR-004:** A request accepted notification is generated for the original sender when a neighbour request is accepted.
- **NOT-BR-005:** Only the notification recipient can mark the notification as read.
- **NOT-BR-006:** A post comment notification is generated for the post author when another eligible user comments on the post.
- **NOT-BR-007:** A post reaction notification is generated for the post author when another eligible user adds a reaction to the post.
- **NOT-BR-008:** The platform must not generate post activity notifications for the actor who created the comment or reaction.
