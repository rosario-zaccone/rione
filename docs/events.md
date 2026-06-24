# Event Flow
**Version:** v0.1.0  
**Last Updated:** 2026-06-24

This document describes the event-based notification flow implemented between the social service and the notification service.

## Overview

The social service publishes neighbor request events to Kafka. The notification service consumes those events from the social topic and stores in-app notifications for the user who must be notified.

The implemented events are:

- `REQUEST_RECEIVED`: emitted when a user sends a neighbor request.
- `REQUEST_ACCEPTED`: emitted when a received neighbor request is accepted.

## Kafka Topic

All social events are published to this topic:

```text
social
```

The topic name is configurable through:

```text
RIONE_SOCIAL_TOPIC
```

Both services use Kafka bootstrap servers configured through:

```text
KAFKA_BOOTSTRAP_SERVERS
```

## Event Payload

Events are published as JSON strings. The current payload shape is:

```json
{
  "type": "REQUEST_RECEIVED",
  "requestId": 10,
  "senderId": 1,
  "receiverId": 2,
  "occurredAt": "2026-06-24T22:00:00"
}
```

Fields:

- `type`: event type, either `REQUEST_RECEIVED` or `REQUEST_ACCEPTED`.
- `requestId`: neighbor request identifier.
- `senderId`: user who originally sent the neighbor request.
- `receiverId`: user who received and may accept the neighbor request.
- `occurredAt`: time when the event was published.

## Social Service Publishing

The social service now has an output port named `SocialEventPublisher`.

The application service publishes events only after the corresponding social state change succeeds:

- After `sendNeighborRequest`, it publishes `REQUEST_RECEIVED`.
- After `acceptNeighborRequest`, it publishes `REQUEST_ACCEPTED`.

The Kafka adapter is `KafkaSocialEventPublisher`. It serializes the event as JSON and sends it to the `social` topic.

Kafka message keys are set to the notification recipient:

- `REQUEST_RECEIVED`: key is `receiverId`.
- `REQUEST_ACCEPTED`: key is `senderId`.

## Notification Service Consumption

The notification service listens to the `social` topic with `SocialEventListener`.

Recipient mapping:

- `REQUEST_RECEIVED` creates a notification for the receiver, with the sender as actor.
- `REQUEST_ACCEPTED` creates a notification for the original sender, with the receiver as actor.

The notification service stores notifications in PostgreSQL through a JPA adapter.

## Notification API

The notification service exposes read-side HTTP endpoints:

```text
GET /notifications/users/{userId}
PATCH /notifications/users/{userId}/{notificationId}/read
```

The API gateway routes `/notifications/**` to the notification service.

## Local Docker Compose

The local compose setup now includes:

- `kafka`
- `notification-postgres`
- `notification-service` configured with Kafka and PostgreSQL
- `social-service` configured with Kafka

Kafka auto-creates topics in local development.

## Tests

The implementation includes:

- Social service unit tests proving events are published after successful request creation and acceptance.
- Social service Kafka adapter tests proving the JSON contract and message key behavior.
- Notification service unit tests for notification creation and read marking.
- Notification service Cucumber acceptance tests for the two business rules.
- Notification service architecture tests for hexagonal boundaries.
- Notification service integration tests for the Kafka listener and HTTP controller.
