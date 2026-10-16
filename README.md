# Social Activity Planner — Backend (community-service)

Backend for a platform that helps a group of friends agree on one practical activity together. The service manages users, groups, group membership, and group chat messages. It is the supporting product layer around the planning engine described in the thesis, and it must stay focused on group activity planning rather than growing into a general social network.

## Goal of this service

- Register and manage user profiles.
- Let users create and join groups, and manage who is in each group and with what role (`OWNER`, `ADMIN`, `MEMBER`).
- Provide a real-time, per-group chat so members can discuss a plan.
- Serve as the home for the planning workflow: collecting member preferences, generating ranked plans, and accepting a plan into an activity. These are not implemented yet.

Out of scope for the MVP: feeds, followers, likes, background location tracking, payments, and multi-stop itineraries.

## Thesis context

The project is a bachelor thesis on group activity planning under heterogeneous preferences and constraints.

- **RQ1:** How should individual preferences be represented so they can be combined at group level?
- **RQ2:** How can the planner resolve member conflicts while respecting hard constraints?
- **RQ3:** How does the approach perform as group size and preference conflict increase?

The planning engine will be a pure Java service behind a `PlanningStrategy` interface, independent of HTTP and database code, so that several strategies (majority voting, average score, least misery, and a fairness-aware weighted planner) can be compared under the same inputs.

## Current status

| Area | Status |
| --- | --- |
| Spring Boot project (Java 21, Spring Boot 4.1.1, Gradle) | Done |
| Database schema via Flyway (`users`, `groups`, `group_members`, `messages`) | Done |
| JPA entities (`User`, `Group`, `GroupMember`, `GroupMemberId`, `GroupRole`, `Message`) | Done |
| Request DTOs (`dto/request`) and response DTOs (`dto/response`) | Done |
| Entity-to-DTO mappers (`mapper`) | Done |
| REST controllers and services | Not started |
| Authentication and authorization | Not started |
| WebSocket group chat | Not started |
| Preference form and planning engine | Not started (planned, separate from this service's CRUD layer) |
| Redis for TTL-limited live location | Planned, optional, last |

## Tech stack

- Java 21, Spring Boot 4.1.1, Gradle (Kotlin DSL)
- Spring Data JPA, Spring Web MVC, Bean Validation, Spring WebSocket
- PostgreSQL with Flyway migrations
- Lombok

## Project structure

```
community-service/src/main/java/com/socialactivityplanner/community_service/
├── entity/     JPA entities mapped to the database schema
├── dto/
│   ├── request/   Validated input records (create/update requests)
│   └── response/  Output records returned to clients (no password hashes)
├── mapper/     Static converters from entities to response DTOs
└── repository/ Spring Data repositories
```

Database migrations live in `community-service/src/main/resources/db/migration/`.

## Planned REST API

All endpoints are under `/api/v1`. This is a proposal; see `docs/api-plan.md`.

| Resource | Endpoints |
| --- | --- |
| Users | `POST /users`, `GET /users/me`, `PATCH /users/me` |
| Groups | `POST /groups`, `GET /groups`, `GET /groups/{groupId}`, `PATCH /groups/{groupId}`, `DELETE /groups/{groupId}` |
| Group members | `POST /groups/{groupId}/members`, `GET /groups/{groupId}/members`, `DELETE /groups/{groupId}/members/me` |
| Messages | `POST /groups/{groupId}/messages`, `GET /groups/{groupId}/messages`, `PATCH /groups/{groupId}/messages/{messageId}`, `DELETE /groups/{groupId}/messages/{messageId}` |

Open decisions: authentication rules, error response format, pagination and sorting, and whether membership is required to read or send messages.

## Running locally

Requires JDK 21 and a running PostgreSQL instance configured for the `dev` profile (see `src/main/resources/application-dev.yaml`).

```bash
cd community-service
./gradlew bootRun
./gradlew test
```

## Evaluation (planned)

The planning engine will be measured on preference satisfaction, fairness (minimum satisfaction and spread), hard-constraint violations, planning latency, and explainability, using synthetic scenarios of 3, 5, 10, 30, and 100 members at low, medium, and high conflict. A user study with 10–20 participants will compare generated plans against the group's own decisions.
