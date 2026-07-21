# Context Map
**Version:** v0.1.1
**Last Updated:** 2026-07-21

This document maps the Bounded Contexts of Rione and the DDD integration patterns used between them, complementing the service boundaries described in `design_choices.md` and the async flow in `events.md`.

## Bounded Contexts

| Bounded Context | Service | Core responsibility |
|---|---|---|
| Identity & Neighborhood | `user-service` | Users, cities, neighborhoods, authentication |
| Social Graph | `social-service` | Neighbor requests, neighbor relationships |
| Content | `post-service` | Posts, comments, reactions (event-sourced) |
| Notification | `notification-service` | In-app notifications |

## Relationships

```
user-service --(ACL, sync REST)--> social-service
user-service --(ACL, sync REST)--> post-service
social-service --(ACL, sync REST)--> post-service
social-service --(OHS/Published Language, async Kafka)--> notification-service
```

### user-service ↔ social-service / post-service — Anticorruption Layer

`social-service` and `post-service` are each upstream-dependent on `user-service` for identity/neighborhood data, but neither adopts `user-service`'s model directly. Each downstream context defines its own outbound port in its own ubiquitous language and a private adapter that translates the upstream response:

- `social-service`: ports `NeighborhoodMembership` / `UserDirectory`, adapter `UserServiceProxy` — calls `GET /internal/users/{userId}/neighborhood`, `/internal/users/profiles`, `/internal/users/search` on `user-service`, and maps the raw `UserResponse`/`UserSearchResponse` payloads into its own `UserProfile`/`UserId` types.
- `post-service`: port `UserServiceProfileProxy` — calls the same `user-service` internal endpoints to resolve author profiles for post/comment display.

Because the translation happens entirely inside the adapter and the downstream domain never sees `user-service`'s JSON shape, this is an **Anticorruption Layer**, not a Conformist relationship.

### social-service ↔ post-service — Anticorruption Layer

`post-service` depends on `social-service` to resolve relationship visibility for posts (same-neighborhood, active neighborship, blocked status):

- port `PostVisibilityChecker`, adapter `SocialPostVisibilityProxy` — calls `GET /internal/social/post-visibility` and translates the response into `post-service`'s own `RelationshipVisibility` value object.

Same pattern as above: `post-service` is the customer, `social-service` is the supplier, and the adapter isolates `post-service`'s domain from `social-service`'s wire format.

All internal REST integrations share the same shape: a dedicated `Internal*Controller` on the supplier side (`/internal/...`, service-to-service JWT auth) and a private `*Proxy` adapter plus circuit breaker (Resilience4j) on the customer side.

### social-service → notification-service — Open Host Service / Published Language

`social-service` publishes domain events (`REQUEST_RECEIVED`, `REQUEST_ACCEPTED`) as JSON to the Kafka topic `social`. `notification-service` consumes them independently via `SocialEventListener`. The JSON event schema is the **Published Language**; the topic plus stable event shape is the **Open Host Service** — `social-service` doesn't know or care who else might consume the topic. Full schema and field-level detail are in `docs/events.md`.

## Notes

- No **Shared Kernel** exists between services: `common` only holds marker annotations (`@DDDAggregateRoot`, etc.), not shared domain model or business logic.
- No **Partnership** or **Conformist** relationships currently exist; every cross-context dependency goes through an explicit ACL or an event-based Published Language, keeping each service's domain model independent as required by the hexagonal boundary rules (see `architecture/` ArchUnit tests per service).
