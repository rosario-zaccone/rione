# Design Choices
**Version:** v0.1.2  
**Last Updated:** 2026-07-27

This document summarizes the main architectural and design choices for the project.

## Architecture

- **General architecture:** Microservices
- **Communication style:** Event-driven communication where asynchronous workflows are required
- **Event-driven service:** Post service

## Design Approach

- **Domain model:** Domain-Driven Design (DDD)
- **Service structure:** Hexagonal architecture

## Microservice Patterns

- API Gateway
- Health Check
- Application Metrics
- Log Aggregation
- Event Sourcing for the post service
- Circuit Breaker
- Database per Service

## Deployment

- **Containerization:** each microservice is packaged as a Docker image.
- **Composition:** Docker Compose is used to run the microservices and supporting infrastructure together.
- **Deployment boundary:** each microservice remains independently buildable and deployable.
