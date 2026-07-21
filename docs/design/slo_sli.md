# Service Level Objectives and Indicators
**Version:** v0.2.0  
**Last Updated:** 2026-07-21

This document adapts the course assignment "define 2 SLOs and the corresponding SLIs, extending the implementation to measure them" (originally framed around the "Shipping on the Air" case study) to Rione's own domain and architecture.

## Why these two

The assignment's case study centers on something being dispatched and expected to arrive reliably and within a bounded time. Rione has no shipping domain, but it has two comparable concerns:

- a public entry point (the api-gateway) whose overall availability matters to every client request, and
- an asynchronous "dispatch and delivery" flow: `social-service` publishes neighbor-request events to Kafka, and `notification-service` consumes them and persists a notification. An event that is published but arrives late (or is never reflected as a notification) is the direct analogue of a shipment that doesn't arrive on time. See `docs/events.md` for the full event flow.

## SLO 1 — API Gateway availability

**SLO:** 99.5% of requests handled by the api-gateway over a rolling window return a non-5xx response.

**SLI:** ratio of non-5xx responses to total responses, computed from the `http_server_requests_seconds_count` metric that Spring Boot Actuator/Micrometer already exposes on every service via `/actuator/prometheus` (scraped by Prometheus, see `monitoring/prometheus/prometheus.yml`).

```promql
sum(rate(http_server_requests_seconds_count{job="api-gateway", status!~"5.."}[$__rate_interval]))
/
sum(rate(http_server_requests_seconds_count{job="api-gateway"}[$__rate_interval]))
```

No application code was needed for this one — the metric already existed. Visualized in the "SLO — Gateway Success Rate (target 99.5%)" panel on the "Rione Microservices" Grafana dashboard.

## SLO 2 — Neighbor-request notification delivery latency

**SLO:** 95% of `REQUEST_RECEIVED`/`REQUEST_ACCEPTED` events published to the `social` Kafka topic are consumed and persisted as a notification within 2 seconds of publication.

**SLI:** ratio of events whose consumption latency falls within the 2-second bucket, over all consumed events.

```promql
sum(rate(rione_social_event_delivery_seconds_bucket{le="2.0", job="notification-service"}[$__rate_interval]))
/
sum(rate(rione_social_event_delivery_seconds_count{job="notification-service"}[$__rate_interval]))
```

### Measurement

`rione_social_event_delivery_seconds` is a new Micrometer `Timer`, registered in `KafkaSocialEventListener` (`services/notification-service/src/main/java/com/rione/notification/infrastructure/messaging/KafkaSocialEventListener.java`). It uses Micrometer's `serviceLevelObjectives(Duration.ofSeconds(2))`, which makes Micrometer publish a Prometheus histogram bucket at exactly the 2-second SLO threshold. On every consumed message, the listener records `Duration.between(event.occurredAt(), now)` (negative durations, from clock skew between containers, are clamped to zero). The service's `Clock` is injected (`KafkaConsumerConfiguration#clock`) so the behavior is deterministic in tests — see `KafkaSocialEventListenerTest#recordsDeliveryLatencyAgainstTheTwoSecondSlo`.

Visualized in the "SLO — Notification Delivery ≤2s (target 95%)" panel on the same Grafana dashboard.

## Where to look

- Grafana: `http://localhost:3000` → "Rione Microservices" dashboard → the two "SLO — ..." panels.
- Raw metric: `curl http://localhost:8084/actuator/prometheus | grep rione_social_event_delivery_seconds` (notification-service) and `curl http://localhost:8080/actuator/prometheus | grep http_server_requests_seconds_count` (api-gateway).
