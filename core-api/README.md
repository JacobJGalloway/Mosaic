# core-api

Frontend-facing Spring Boot REST entry point. Owns request validation, the JWT/action-ID auth filter chain, CORS, and Kafka consumption; delegates all persistence/business logic to `core-domain`.

## Running it

```
mvn org.springframework.boot:spring-boot-maven-plugin:3.3.4:run
```
(run from inside `core-api/`, or `mvn -pl core-api -am spring-boot:run` from the repo root once `spring-boot-maven-plugin` resolves via the reactor). Needs `MONGODB_URI` set and a reachable Kafka broker (`docker compose up -d` from the repo root) — see the root [README.md](../README.md) for full prerequisites.

On an empty `users` collection, `DevAuthSeeder` bootstraps one `ADMIN` role (every known action ID) and one `admin` / `changeme123` user on startup, logged as a warning. **Dev-only** — not meant to survive as-is past Sprint 2.

## Environment variables

| Variable | Default | Purpose |
|---|---|---|
| `MONGODB_URI` | *(required)* | Mongo connection string |
| `MOSAIC_JWT_SECRET` | dev placeholder | HMAC signing key for JWTs — override before any real deployment |
| `MOSAIC_WEB_ORIGIN` | `http://localhost:5173` | CORS-allowed origin for the frontend (Vite's default dev port) |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |

## REST endpoints

- **`/api/auth`** — `POST /register`, `POST /login`, `POST /refresh` (all under `permitAll`, but `/refresh` requires an actual authenticated request — see `AuthController`).
- **`/api/users`** — admin endpoints gated behind the `USER_MANAGE` action: `PUT /{id}/overrides`, `POST /{id}/deactivate`, `POST /{id}/reactivate`.
- **`/api/clients`**, **`/api/policies`** — CRUD-ish endpoints, each protected by `@PreAuthorize("hasAuthority('...')")` against the matching `ActionIds` constant (`CLIENT_CREATE`/`CLIENT_READ`, `POLICY_CREATE`/`POLICY_READ`).

## Auth backbone (`com.mosaic.api.security`, `com.mosaic.api.auth`)

Full design rationale lives in ARCHITECTURE.md's Auth Design section — this is the implementation summary:

- **`JwtService`** — issues/verifies the JWT via jjwt. Claims: `sub` (userId), `actions` (the resolved action-ID list from `UserService.resolveActions`), `tokenVersion`, 30-minute `exp`.
- **`JwtAuthenticationFilter`** — the three-part protected-request check: (1) JWT signature + expiry valid, (2) the token's `tokenVersion` claim matches the user's current stored value (the forced-refresh/immediate-invalidation mechanism), (3) turns the token's `actions` claim into Spring Security `GrantedAuthority`s so ordinary `@PreAuthorize("hasAuthority(...)")` does the per-action-ID check.
- **`SecurityConfig`** — stateless filter chain, CORS, `BCryptPasswordEncoder` bean, `HttpStatusEntryPoint(401)` for missing/invalid auth (reserving 403 for "authenticated but lacks the action ID"). Also permits `/error` — without it, a `ResponseStatusException`'s internal error-dispatch forward gets re-blocked by the security chain, silently downgrading the intended status code to an empty 403.

## Kafka (`com.mosaic.api.kafka`)

`IngestionRoutingConsumer`/`McpLifecycleEventsConsumer` are throwaway consumers (just log what arrives) proving the `ingestion-routing`/`mcp-lifecycle-events` topics are wired end-to-end. Real consumption of `ingestion-routing` happens in `ollama-mcp-server`.

## Testing

`JwtServiceTest` (JUnit 5 + AssertJ) — round trip, foreign-signature rejection, expiry. Run with `mvn test -pl core-api` from the repo root. Not yet covered: controller-level/integration tests for the full filter chain — see `docs/SPRINT2_CHECKLIST.md`.
