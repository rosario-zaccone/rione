# User Stories
**Version:** v0.3.1  
**Last Updated:** 2026-06-17

This document collects user stories, acceptance criteria, and business rules.

## Story Template

**As a** [type of user]  
**I want** [goal or capability]  
**So that** [benefit or reason]

**Acceptance Criteria**
- [Expected observable behavior]
- [Relevant validation or rule]
- [Expected error or edge-case behavior]

## User Stories

### US-001 - Sign Up

**As a** visitor  
**I want** to create an account  
**So that** I can access the platform as a registered user

**Acceptance Criteria**
- A visitor can sign up by providing valid account and profile information.
- The platform rejects duplicate email addresses and duplicate usernames.
- The platform stores the password securely and never exposes it in responses.
- After successful sign up, the user account is available for authentication.

### US-002 - Log In

**As a** registered user  
**I want** to log in with my email and password  
**So that** I can securely access my account

**Acceptance Criteria**
- A registered user can log in with a valid email and password.
- The platform rejects invalid credentials without revealing which field is incorrect.
- The platform denies login for accounts that are blocked, deleted, or otherwise disabled.
- A successful login provides the user with an authenticated session or token.

### US-003 - Update Profile

**As a** registered user  
**I want** to update my profile info (name, surname, username, neighborhood, birth date, bio)  
**So that** my neighbours can recognize me and see accurate information about me

**Acceptance Criteria**
- A registered user can update their editable profile information.
- The platform validates required fields, username uniqueness, birth date, and bio length.
- Profile changes are visible to other users according to privacy and connection rules.
- Invalid profile updates are rejected without changing the existing profile.

### US-004 - Search Neighbours

**As a** registered user  
**I want** to search for neighbours and see their profile information  
**So that** I can find people in my neighbourhood and decide who to connect with

**Acceptance Criteria**
- A registered user can search users in their own neighborhood by supported profile fields.
- Search results exclude blocked users and users who should not be visible to the requester.
- Search results show only profile information allowed by privacy rules.
- Empty searches or no-match searches return an empty result set instead of an error.

### US-005 - Send Neighbour Request

**As a** registered user  
**I want** to send a neighbour request to another user  
**So that** we can become connected once they accept

**Acceptance Criteria**
- A registered user can send a neighbour request to a visible user in the same neighborhood.
- The platform prevents duplicate pending requests between the same users.
- The platform prevents users from sending requests to themselves or blocked users.
- The recipient receives a notification when the request is created.

### US-006 - Respond to Neighbour Request

**As a** registered user  
**I want** to accept or decline a neighbour request  
**So that** I can decide whether to connect with another user

**Acceptance Criteria**
- A recipient can accept or decline a pending neighbour request addressed to them.
- Accepting a request creates a neighbour connection between both users.
- Declining a request closes it without creating a connection.
- The platform rejects responses to expired, already handled, or unauthorized requests.

### US-007 - Remove Neighbour

**As a** registered user  
**I want** to remove a neighbour from my contacts  
**So that** I can keep my neighbourhood contacts relevant and up to date

**Acceptance Criteria**
- A registered user can remove an existing neighbour connection.
- Removing a neighbour deletes the connection for both users.
- Removing a neighbour does not delete historical posts, comments, or reactions.
- The platform handles attempts to remove a non-existing connection without creating duplicate side effects.

### US-008 - Block Neighbour

**As a** registered user  
**I want** to block a neighbour  
**So that** they can no longer contact me or see my private activity

**Acceptance Criteria**
- A registered user can block another user who is visible to them.
- Blocking a user removes any existing neighbour connection and pending requests between the two users.
- A blocked user cannot send neighbour requests, comments, reactions, or direct interactions to the blocker.
- Blocking does not remove already published public or moderation-required records.

### US-009 - Create Post

**As a** registered user  
**I want** to create posts about warnings, requests for help, events, or discussions  
**So that** I can inform and interact with my neighbours

**Acceptance Criteria**
- A registered user can create a post in their neighborhood with a supported post type.
- The platform validates post content, required fields, and minimum content length.
- A created post is visible to eligible neighbours according to visibility and blocking rules.
- Invalid posts are rejected without publishing partial content.

### US-010 - React to Post

**As a** registered user  
**I want** to react to posts  
**So that** I can share my opinion and feedback

**Acceptance Criteria**
- A registered user can add one supported reaction to an eligible post.
- The platform prevents more than one active reaction by the same user on the same post.
- The platform rejects reactions to unavailable posts or posts hidden by blocking rules.
- The post score or reaction summary is updated after the reaction is added.

### US-011 - Update Reaction

**As a** registered user  
**I want** to update my reaction to a post  
**So that** I can change my feedback when my opinion changes

**Acceptance Criteria**
- A registered user can change their existing reaction on an eligible post.
- The platform updates the previous reaction instead of creating a second reaction.
- The platform rejects updates when the user has not reacted to the post.
- The post score or reaction summary reflects the updated reaction.

### US-012 - Remove Reaction

**As a** registered user  
**I want** to remove my reaction from a post  
**So that** I can withdraw feedback I no longer want to share

**Acceptance Criteria**
- A registered user can remove their own reaction from an eligible post.
- Removing a reaction updates the post score or reaction summary.
- The platform rejects attempts to remove another user's reaction.
- Removing a non-existing reaction does not create a new reaction or change the post content.

### US-013 - Add Comment

**As a** registered user  
**I want** to add comments to posts  
**So that** I can contribute to discussions with my neighbours

**Acceptance Criteria**
- A registered user can add a comment to an eligible post.
- The platform validates comment content and minimum content length.
- A created comment is visible to eligible users according to post visibility and blocking rules.
- Invalid comments are rejected without publishing partial content.

### US-014 - Update Comment

**As a** registered user  
**I want** to update my comments  
**So that** I can correct mistakes or clarify what I wrote

**Acceptance Criteria**
- A registered user can update only comments they authored.
- The platform validates the updated comment content and minimum content length.
- The platform keeps the comment associated with the original post and author.
- Unauthorized or invalid updates are rejected without changing the existing comment.

### US-015 - Remove Comment

**As a** registered user  
**I want** to remove my comments  
**So that** I can delete contributions I no longer want to keep visible

**Acceptance Criteria**
- A registered user can remove only comments they authored.
- Removed comments are no longer visible in normal post discussions.
- Removing a comment does not remove the parent post.
- Unauthorized delete attempts are rejected without changing the comment.

### US-016 - Receive Notifications

**As a** registered user  
**I want** to receive notifications  
**So that** I can stay updated about reactions and comments on my posts, neighbour requests, and moderation information

**Acceptance Criteria**
- A registered user receives notifications for relevant neighbour requests, post interactions, and moderation events.
- The platform does not send notifications for interactions hidden by blocking or visibility rules.
- A user can view unread and read notification states.
- Notification delivery failures do not roll back the original user action that triggered the notification.

## Business Rules

- BR-001: A user must have a unique email address and a unique username.
- BR-002: A user can belong to one neighborhood at a time.
- BR-003: Neighbour requests can only be sent between users in the same neighborhood.
- BR-004: A user cannot send a neighbour request to themselves.
- BR-005: Only one pending neighbour request can exist between the same two users at the same time.
- BR-006: Accepting a neighbour request creates a reciprocal neighbour connection.
- BR-007: Declining, removing, or blocking a neighbour does not delete historical posts, comments, reactions, or notifications.
- BR-008: Blocking takes precedence over neighbour connections, search visibility, requests, reactions, comments, and notifications.
- BR-009: Posts can only be created by authenticated registered users.
- BR-010: Posts must belong to one supported type: warning, help, event, or discussion.
- **BR-011: Post content must contain at least 20 non-whitespace characters to encourage meaningful communication and match short attention spans.**
- BR-012: Comments can only be created by authenticated registered users on posts they are allowed to view.
- **BR-013: Comment content must contain at least 10 non-whitespace characters to avoid empty or low-signal discussion while remaining quick to read.**
- BR-014: A user can have only one active reaction on a post.
- BR-015: A user can update or remove only their own comments and reactions.
- BR-016: Notifications are generated for relevant user actions, but notification delivery must not block the original action.
- **BR-017: User biography content must contain at least 20 non-whitespace characters so profiles are meaningful while staying quick to scan.**
