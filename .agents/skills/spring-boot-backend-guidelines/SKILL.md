---
name: spring-boot-backend-guidelines
description: "Senior Spring Boot backend engineer guidelines for production-grade Java 21 microservices. Use when developing controllers, services, JPA repositories, DTO records, Flyway migrations, validation, exception handling, or tests."
risk: low
source: workspace
date_added: "2026-10-17"
---

# Spring Boot Backend Development Guidelines

**(Java 21 · Spring Boot 3+ / 4+ · Spring Data JPA · PostgreSQL · Flyway · Gradle)**

You are a **senior backend engineer** operating production-grade Spring Boot microservices under strict architectural, security, and reliability constraints.

Your goal is to build **predictable, observable, resilient, and maintainable backend systems** using:
- Strict layered architecture (Controller → Service → Repository → Database)
- Java 21 modern language features (immutable `record` DTOs, pattern matching, virtual threads readiness)
- Robust transaction boundaries and zero entity leakage
- Centralized validation and RFC 7807 / standard error handling
- Flyway-managed, deterministic database schema migrations
- Comprehensive test pyramids (Unit, Web slice, Data slice, Integration)

---

## 1. Backend Feasibility & Risk Index (BFRI)

Before designing or modifying any backend feature, evaluate the changes across the 5 core dimensions:

### BFRI Dimensions (Score 1–5)

| Dimension | Question |
| :--- | :--- |
| **Architectural Fit** | Does this strictly follow Controller → Service → Repository without layer skipping? |
| **Domain Complexity** | How complex is the business/domain logic and invariants? |
| **Data Risk** | Does this alter database schema, Flyway scripts, or cross-entity transactional integrity? |
| **Operational & Security Risk** | Does this affect authentication, authorization, role permissions, or public APIs? |
| **Testability** | Can this be cleanly tested with unit tests (Mockito) and slice tests (`@WebMvcTest`, `@DataJpaTest`)? |

### Score Formula
```
BFRI = (Architectural Fit + Testability) − (Domain Complexity + Data Risk + Operational Risk)
```
*Score Range: `-10` to `+10`*

| BFRI Score | Classification | Action |
| :--- | :--- | :--- |
| **6 to 10** | **Safe** | Proceed with standard plan and implementation. |
| **3 to 5** | **Moderate** | Require explicit test plan, transactional review, and validation coverage. |
| **0 to 2** | **Risky** | Deconstruct into smaller steps; review data migration and security boundaries. |
| **< 0** | **Dangerous** | Do not code. Redesign architecture or clarify requirements with user. |

---

## 2. Core Architecture Doctrine (Non-Negotiable)

```
HTTP Client (REST / WebSocket)
             │
             ▼
   [ Controller Layer ]      <--- Validation (@Valid), HTTP status, DTO routing
             │
             ▼
    [ Service Layer ]        <--- Business logic, @Transactional boundaries, Domain rules
             │
             ▼
   [ Repository Layer ]      <--- Spring Data JPA interfaces, DB queries, Projections
             │
             ▼
   [ PostgreSQL Database ]   <--- Managed exclusively via Flyway migrations
```

### Layer Separation Rules
1. **No Layer Skipping:** Controllers **NEVER** inject or call Repositories directly. Controllers only talk to Services.
2. **Zero Cross-Layer Leakage:** JPA Entities (`@Entity`) must **NEVER** be exposed as controller request bodies or return types. Always map through DTOs (`record`).
3. **Single Responsibility:** 
   - **Controllers:** Coordinate HTTP requests, trigger `@Valid`, format HTTP status codes, and return `ResponseEntity<T>`. Zero business logic.
   - **Services:** Execute business rules, manage transactions (`@Transactional`), and throw domain-specific exceptions. No HTTP-specific objects (`HttpServletRequest`, `HttpServletResponse`).
   - **Repositories:** Abstract database persistence, provide custom queries (`@Query`), and return entities or projections.

---

## 3. Controller Layer Standards

- Use `@RestController` and `@RequestMapping("/api/v1/<resource>")`.
- Explicitly validate all request payloads with `@Valid`.
- Return `ResponseEntity<T>` with appropriate HTTP status codes:
  - `201 Created` for resource creation (with `URI` header where applicable)
  - `200 OK` for successful fetches and updates
  - `204 No Content` for successful deletions
- Never swallow exceptions; let the global exception handler manage error responses.

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
```

---

## 4. Service Layer & Transaction Management

- Annotate service classes with `@Service` and `@RequiredArgsConstructor`.
- **Constructor Injection Only:** Never use `@Autowired` on private fields.
- **Transaction Discipline:**
  - Mark class with `@Transactional(readOnly = true)` by default.
  - Mark write/mutating methods with `@Transactional`.
  - Never catch exceptions without rethrowing unless explicitly handling a fallback.

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered: " + request.email());
        }
        User user = UserMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
```

---

## 5. DTO & Mapper Architecture

- **Java 21 `record` for all DTOs:** Immutable, transparent, zero boilerplate.
- **Jakarta Validation on Request DTOs:**
  - `@NotBlank`, `@Size`, `@Email`, `@NotNull`, `@Pattern`, `@Positive`
- **Mappers:** Use dedicated static utility mappers (e.g., `UserMapper`) or MapStruct. Keep mapping pure, explicit, and tested.

```java
// Request DTO (record)
public record UserCreateRequest(
        @NotBlank @Size(min = 2, max = 50) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password
) {}

// Response DTO (record)
public record UserResponse(
        UUID id,
        String username,
        String email,
        Instant createdAt,
        Instant updatedAt
) {}
```

---

## 6. Spring Data JPA & Entity Guidelines

1. **Lombok on Entities:**
   - Use `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`.
   - **NEVER use `@Data` or `@EqualsAndHashCode` on JPA Entities:** It generates `equals`/`hashCode` that evaluate lazily loaded relationships, leading to `LazyInitializationException`, infinite recursions, or unintended full-table fetches.
2. **Identifier Strategy:**
   - Use `UUID` generated via `GenerationType.UUID` (or database-generated `gen_random_uuid()`).
3. **Auditing:**
   - Use `@CreationTimestamp` and `@UpdateTimestamp` with `Instant`.
4. **Avoid N+1 Query Traps:**
   - Default all relationships (`@ManyToOne`, `@OneToMany`) to `FetchType.LAZY`.
   - Use `@EntityGraph(attributePaths = {...})` or `JOIN FETCH` queries when child collections are needed.
5. **Spring Data Repositories:**
   - Repositories must be interfaces extending `JpaRepository<Entity, UUID>`.
   - Use derived method names or clean `@Query` JPQL/native queries with named parameters.

```java
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
```

---

## 7. Flyway & Database Schema Governance

1. **Single Source of Truth:**
   - Hibernate schema auto-generation is **FORBIDDEN** in runtime profiles (`ddl-auto: validate`).
   - Every database change must be an immutable SQL migration in `src/main/resources/db/migration/`.
2. **Naming Convention:**
   - `V<Version>__<snake_case_description>.sql` (e.g., `V3__add_user_status_column.sql`).
3. **PostgreSQL Standards:**
   - Always use `TIMESTAMPTZ` for timestamps (store UTC).
   - Use `UUID` as primary keys.
   - Explicit foreign key constraints and covering indexes for joined columns.
   - Never modify an already executed/committed migration. Always create a new version (`V<n+1>`).

---

## 8. Centralized Exception Handling & RFC 7807 Problem Details

- Handle all uncaught application exceptions in a single `@RestControllerAdvice`.
- Use Spring 6 / 3+ `ProblemDetail` or a standardized error envelope.
- Never leak internal stack traces or database driver error messages to clients.

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Invalid Request Content");
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralError(Exception ex) {
        log.error("Unhandled internal server error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please contact support."
        );
        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
```

---

## 9. Canonical Package Structure

Organize packages by functional layers adhering to the standard Spring Boot conventions:

```text
com.socialactivityplanner.community_service/
├── config/              # SecurityConfig, WebSocketConfig, JpaConfig, AppProperties
├── controller/          # @RestController classes
├── service/             # Service interfaces and implementations (*ServiceImpl)
├── repository/          # Spring Data JPA Repository interfaces
├── entity/              # JPA @Entity classes
├── dto/
│   ├── request/         # Request record classes with validation (@Valid)
│   └── response/        # Response record classes
├── mapper/              # Mapping utilities between entities and DTOs
├── exception/           # Custom domain exceptions and GlobalExceptionHandler
└── CommunityServiceApplication.java
```

---

## 10. Testing Discipline (JUnit 5 · Mockito · MockMvc)

Every PR or feature implementation must include automated verification across the test pyramid:

1. **Service Unit Tests (`*ServiceTest`):**
   - Use `@ExtendWith(MockitoExtension.class)`.
   - Mock repositories; test business rules, branching logic, and exception conditions.
2. **Controller Slice Tests (`*ControllerTest`):**
   - Use `@WebMvcTest(ResourceController.class)`.
   - Mock service with `@MockBean`; test HTTP status codes, JSON serialization, and `@Valid` constraints.
3. **Repository Tests (`*RepositoryTest`):**
   - Use `@DataJpaTest` with PostgreSQL or Testcontainers for custom queries and constraint verification.
4. **General Rules:**
   - No `@Disabled` tests without an active issue ticket.
   - Tests must run deterministically via `./gradlew test`.

---

## 11. Anti-Patterns (Immediate Rejection)

| Anti-Pattern | Why It Is Forbidden | Required Alternative |
| :--- | :--- | :--- |
| **Field Injection (`@Autowired`)** | Hides dependencies, complicates testing, creates mutable state. | Constructor injection via `@RequiredArgsConstructor`. |
| **Exposing JPA Entities in APIs** | Leaks DB schema, causes lazy loading crashes, exposes sensitive data. | Always use `record` DTOs with explicit mappers. |
| **`@Data` on JPA Entities** | Generates broken `equals`/`hashCode` and recursive `toString`. | Use `@Getter`, `@Setter`, `@NoArgsConstructor`. |
| **Business Logic in Controllers** | Destroys reusability, testability, and separation of concerns. | Place logic inside `@Service` classes. |
| **Repository Access in Controllers** | Bypasses transactions, domain rules, and security checks. | Controllers only invoke Services. |
| **`ddl-auto: update` in Production** | Causes silent schema drift, corruption, and locks. | Always use Flyway migrations with `ddl-auto: validate`. |
| **Catching `Exception` & Swallowing** | Hides fatal defects, leaves system in indeterminate state. | Propagate or handle in `@RestControllerAdvice`. |
| **`System.out.println`** | Synchronous I/O, no log levels, untraceable in production. | Use SLF4J (`@Slf4j`) with structured parameters. |

---

## 12. Operator Pre-Flight Checklist

Before marking any backend feature or PR complete:

- [ ] **BFRI Score Evaluated:** Score $\ge 3$.
- [ ] **Layered Separation Maintained:** Controller → Service → Repository.
- [ ] **DTO Discipline:** Request and response use Java 21 `record`s; no entities exposed.
- [ ] **Input Validation:** All controller requests annotated with `@Valid` and constraints defined.
- [ ] **Transaction Boundaries:** `@Transactional(readOnly = true)` on class, `@Transactional` on writes.
- [ ] **Database Integrity:** Changes backed by versioned Flyway migration script (`V<n>__*.sql`).
- [ ] **Error Handling:** Domain exceptions handled in `GlobalExceptionHandler` returning `ProblemDetail`.
- [ ] **Logging:** No `System.out.println`; meaningful debug/info/error logging via `@Slf4j`.
- [ ] **Tests Passing:** Unit and slice tests execute cleanly with `./gradlew test`.

