# Post Service Software Requirements Specification
**Version:** v0.4.0  
**Last Updated:** 2026-06-25

## 1. Scope

The Post Service manages neighborhood posts, reactions, and comments.

## 2. Definitions

- **Post:** A message published by a user inside a neighborhood.
- **Public post:** A post visible to eligible users in the author's neighborhood.
- **Private post:** A post visible to its author and to same-neighborhood users with an active neighborship with the author.
- **Comment:** A reply written by a user in response to a post.
- **Reaction:** A simple response that a user adds to a post.
- **Post score:** A community feedback value derived from reactions.
- **Active neighborship:** An accepted and currently active relationship between two users.

## 3. Functional Requirements

| Code | Short Description | Origin |
|------|-------------------|--------|
| PST-FR-001 | Allow a registered user to create valid neighborhood posts of a supported type with public or private visibility. | PST-US-001 |
| PST-FR-002 | Allow a registered user to react to an eligible post while preventing duplicate active reactions. | PST-US-002 |
| PST-FR-003 | Allow a registered user to update their existing reaction without creating a second reaction. | PST-US-003 |
| PST-FR-004 | Allow a registered user to remove their own reaction. | PST-US-004 |
| PST-FR-005 | Allow a registered user to add valid comments to eligible posts. | PST-US-005 |
| PST-FR-006 | Allow a registered user to update comments they authored. | PST-US-006 |
| PST-FR-007 | Allow a registered user to remove comments they authored. | PST-US-007 |
| PST-FR-008 | Allow a registered user to view public posts from users in the same neighborhood, including users without an active neighborship. | PST-US-008 |
| PST-FR-009 | Allow a registered user to view their own posts, including public and private posts. | PST-US-009 |
| PST-FR-010 | Allow a registered user to view posts authored by a specific same-neighborhood user according to public, private, active-neighborship, and blocking rules. | PST-US-010 |

## 4. Business Rules

| Code | Short Description | Origin |
|------|-------------------|--------|
| PST-BR-001 | Posts can only be created by authenticated registered users. | PST-US-001 |
| PST-BR-002 | Posts must belong to one supported type: warning, help, event, or discussion. | PST-US-001 |
| PST-BR-003 | Post content must contain at least 20 non-whitespace characters. | PST-US-001 |
| PST-BR-004 | Comments can only be created by authenticated registered users on posts they are allowed to view. | PST-US-005 |
| PST-BR-005 | Comment content must contain at least 10 non-whitespace characters. | PST-US-005, PST-US-006 |
| PST-BR-006 | A user can have only one active reaction on a post. | PST-US-002, PST-US-003, PST-US-004 |
| PST-BR-007 | A user can update or remove only their own comments and reactions. | PST-US-003, PST-US-004, PST-US-006, PST-US-007 |
| PST-BR-008 | A user can view their own posts regardless of post visibility. | PST-US-009 |
| PST-BR-009 | A user can view public posts authored by users who belong to the same neighborhood. | PST-US-008, PST-US-010 |
| PST-BR-010 | Same-neighborhood public post visibility does not require an active neighborship relationship between the viewer and the post author. | PST-US-008, PST-US-010 |
| PST-BR-011 | A user can view private posts authored by another user only when both users belong to the same neighborhood and have an active neighborship. | PST-US-008, PST-US-010 |
| PST-BR-012 | Each post must be marked as public or private. | PST-US-001 |

## 5. Non-Functional Requirements

- **PST-NFR-001:** The service must preserve user-generated history unless moderation, privacy, or deletion policy requires removal.
- **PST-NFR-002:** The service must enforce post visibility, active-neighborship, and blocking decisions before returning posts or allowing interactions.
- **PST-NFR-003:** The service must reject invalid content with clear validation errors.
