# Post Service Software Requirements Specification
**Version:** v0.1.0  
**Last Updated:** 2026-06-17

## 1. Scope

The Post Service manages neighborhood posts, reactions, and comments.

## 2. Definitions

- **Post:** A message published by a user inside a neighborhood.
- **Comment:** A reply written by a user in response to a post.
- **Reaction:** A simple response that a user adds to a post.
- **Post score:** A community feedback value derived from reactions.

## 3. Functional Requirements

### PST-FR-001 - Create Post

The service shall allow a registered user to create neighborhood posts.

**Acceptance Criteria**
- The post uses one supported type: warning, help, event, or discussion.
- The service validates required fields and content length.
- The created post is visible to eligible neighbours.
- Invalid posts are rejected without publishing partial content.

### PST-FR-002 - React to Post

The service shall allow a registered user to react to an eligible post.

**Acceptance Criteria**
- The user can add one supported reaction to a post.
- The service prevents more than one active reaction by the same user on the same post.
- Reactions to unavailable or hidden posts are rejected.
- The post score or reaction summary is updated.

### PST-FR-003 - Update Reaction

The service shall allow a registered user to update their existing reaction.

**Acceptance Criteria**
- Updating replaces the existing reaction.
- Updating does not create a second reaction.
- Updates are rejected when the user has not reacted to the post.
- The post score or reaction summary reflects the update.

### PST-FR-004 - Remove Reaction

The service shall allow a registered user to remove their own reaction.

**Acceptance Criteria**
- The user can remove only their own reaction.
- The post score or reaction summary is updated.
- Removing a non-existing reaction does not create a new reaction or change post content.

### PST-FR-005 - Add Comment

The service shall allow a registered user to add comments to eligible posts.

**Acceptance Criteria**
- The service validates comment content and minimum content length.
- The created comment is visible according to post visibility and blocking rules.
- Invalid comments are rejected without publishing partial content.

### PST-FR-006 - Update Comment

The service shall allow a registered user to update comments they authored.

**Acceptance Criteria**
- The user can update only their own comments.
- The service validates updated comment content and minimum content length.
- The comment remains associated with the original post and author.
- Unauthorized or invalid updates are rejected.

### PST-FR-007 - Remove Comment

The service shall allow a registered user to remove comments they authored.

**Acceptance Criteria**
- The user can remove only their own comments.
- Removed comments are hidden from normal post discussions.
- Removing a comment does not remove the parent post.
- Unauthorized delete attempts are rejected.

## 4. Business Rules

- **PST-BR-001:** Posts can only be created by authenticated registered users.
- **PST-BR-002:** Posts must belong to one supported type: warning, help, event, or discussion.
- **PST-BR-003:** Post content must contain at least 20 non-whitespace characters.
- **PST-BR-004:** Comments can only be created by authenticated registered users on posts they are allowed to view.
- **PST-BR-005:** Comment content must contain at least 10 non-whitespace characters.
- **PST-BR-006:** A user can have only one active reaction on a post.
- **PST-BR-007:** A user can update or remove only their own comments and reactions.

## 5. Non-Functional Requirements

- **PST-NFR-001:** The service must preserve user-generated history unless moderation, privacy, or deletion policy requires removal.
- **PST-NFR-002:** The service must enforce post visibility and blocking decisions before interactions.
- **PST-NFR-003:** The service must reject invalid content with clear validation errors.

## 6. Traceability

| Requirement | Source Story | Related Business Rules |
|-------------|--------------|------------------------|
| PST-FR-001 | PST-US-001 | PST-BR-001, PST-BR-002, PST-BR-003 |
| PST-FR-002 | PST-US-002 | PST-BR-006 |
| PST-FR-003 | PST-US-003 | PST-BR-006, PST-BR-007 |
| PST-FR-004 | PST-US-004 | PST-BR-006, PST-BR-007 |
| PST-FR-005 | PST-US-005 | PST-BR-004, PST-BR-005 |
| PST-FR-006 | PST-US-006 | PST-BR-005, PST-BR-007 |
| PST-FR-007 | PST-US-007 | PST-BR-007 |
