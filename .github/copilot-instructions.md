# Copilot instructions

## Repository shape

This repository is the start of a modular monolith for the Social Activity Planner bachelor thesis. The root `README.md` is the product and research source of truth: the core problem is combining heterogeneous member constraints and preferences into a practical, explainable group activity plan. The currently implemented slice is the Spring Boot `community-service` module.

The module is a Java 21 Spring Boot application using Gradle Kotlin DSL. `CommunityServiceApplication` is the application entry point in `com.socialactivityplanner.community_service`. The build already includes Spring MVC, validation, JPA, WebSocket, PostgreSQL, and Flyway dependencies, but most domain, HTTP, and messaging features described in the README are not implemented yet.

## Build and test

Run commands from `community-service/` (or prefix them with `cd community-service &&`):

```bash
./gradlew build
./gradlew test
./gradlew test --tests "com.socialactivityplanner.community_service.CommunityServiceApplicationTests"
./gradlew test --tests "com.socialactivityplanner.community_service.CommunityServiceApplicationTests.contextLoads"
./gradlew bootRun
```

Use the checked-in Gradle wrapper and Java 21. There is no dedicated lint or formatting task configured; `check` runs the configured verification tasks.

The default `application.yaml` activates the `dev` profile. The ignored `src/main/resources/application-dev.yaml` supplies the local PostgreSQL connection, and the context test starts Flyway and validates the JPA schema, so PostgreSQL must be reachable with the configured database before running the test or application. Do not commit local `application*.yaml` files or credentials.

## Persistence conventions

Flyway owns schema changes under `src/main/resources/db/migration`; add the next versioned migration rather than relying on Hibernate to create or mutate tables. JPA is configured with `ddl-auto: validate`, so entity mappings must match the migration schema. The initial `users` table uses UUID identifiers, unique username/email values, password hashes (never plaintext passwords), and created/updated timestamps.

## Planner and domain conventions

When implementing the thesis core, keep planning logic as a pure Java service behind a `PlanningStrategy` interface. It must not depend on HTTP, Spring infrastructure, or database code so multiple strategies can be evaluated against the same inputs.

Apply hard constraints first (day, maximum budget, duration range, and maximum travel distance); invalid candidates must not be ranked as valid. Score remaining candidates from weighted member preferences, then combine average satisfaction, minimum-member fairness, normalized cost, and normalized travel time using configurable weights. Results must be deterministic for identical inputs, return the top three candidates, and include explanations for both accepted rankings and rejected candidates.

Keep baseline strategies (majority voting, average score, least misery/maximin, and the fairness-aware weighted planner) comparable under the same input model. Preserve evaluation data needed by the README’s research metrics: exact input, removed candidates, per-member contributions, score components, and execution time.

## Source and package conventions

Use the existing package name `com.socialactivityplanner.community_service`; the underscore is intentional because hyphens are not valid Java package identifiers. Keep new code under the existing module/package structure and follow the current Spring Boot/Gradle conventions. Treat the root README as the authority for MVP boundaries: do not expand the product into feeds, followers, likes, payments, background tracking, or multi-stop itineraries.
