# Post Service User Stories
**Version:** v0.1.0  
**Last Updated:** 2026-06-17

## PST-US-001 - Create Post

**As a** registered user  
**I want** to create posts about warnings, requests for help, events, or discussions  
**So that** I can inform and interact with my neighbours

**Acceptance Criteria**
- A registered user can create a post in their neighborhood with a supported post type.
- The platform validates post content, required fields, and minimum content length.
- A created post is visible to eligible neighbours according to visibility and blocking rules.
- Invalid posts are rejected without publishing partial content.

## PST-US-002 - React to Post

**As a** registered user  
**I want** to react to posts  
**So that** I can share my opinion and feedback

**Acceptance Criteria**
- A registered user can add one supported reaction to an eligible post.
- The platform prevents more than one active reaction by the same user on the same post.
- The platform rejects reactions to unavailable posts or posts hidden by blocking rules.
- The post score or reaction summary is updated after the reaction is added.

## PST-US-003 - Update Reaction

**As a** registered user  
**I want** to update my reaction to a post  
**So that** I can change my feedback when my opinion changes

**Acceptance Criteria**
- A registered user can change their existing reaction on an eligible post.
- The platform updates the previous reaction instead of creating a second reaction.
- The platform rejects updates when the user has not reacted to the post.
- The post score or reaction summary reflects the updated reaction.

## PST-US-004 - Remove Reaction

**As a** registered user  
**I want** to remove my reaction from a post  
**So that** I can withdraw feedback I no longer want to share

**Acceptance Criteria**
- A registered user can remove their own reaction from an eligible post.
- Removing a reaction updates the post score or reaction summary.
- The platform rejects attempts to remove another user's reaction.
- Removing a non-existing reaction does not create a new reaction or change the post content.

## PST-US-005 - Add Comment

**As a** registered user  
**I want** to add comments to posts  
**So that** I can contribute to discussions with my neighbours

**Acceptance Criteria**
- A registered user can add a comment to an eligible post.
- The platform validates comment content and minimum content length.
- A created comment is visible to eligible users according to post visibility and blocking rules.
- Invalid comments are rejected without publishing partial content.

## PST-US-006 - Update Comment

**As a** registered user  
**I want** to update my comments  
**So that** I can correct mistakes or clarify what I wrote

**Acceptance Criteria**
- A registered user can update only comments they authored.
- The platform validates the updated comment content and minimum content length.
- The platform keeps the comment associated with the original post and author.
- Unauthorized or invalid updates are rejected without changing the existing comment.

## PST-US-007 - Remove Comment

**As a** registered user  
**I want** to remove my comments  
**So that** I can delete contributions I no longer want to keep visible

**Acceptance Criteria**
- A registered user can remove only comments they authored.
- Removed comments are no longer visible in normal post discussions.
- Removing a comment does not remove the parent post.
- Unauthorized delete attempts are rejected without changing the comment.

## Business Rules

- **PST-BR-001:** Posts can only be created by authenticated registered users.
- **PST-BR-002:** Posts must belong to one supported type: warning, help, event, or discussion.
- **PST-BR-003:** Post content must contain at least 20 non-whitespace characters.
- **PST-BR-004:** Comments can only be created by authenticated registered users on posts they are allowed to view.
- **PST-BR-005:** Comment content must contain at least 10 non-whitespace characters.
- **PST-BR-006:** A user can have only one active reaction on a post.
- **PST-BR-007:** A user can update or remove only their own comments and reactions.
