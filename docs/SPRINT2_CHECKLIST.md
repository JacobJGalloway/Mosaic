# Sprint 2 Checklist

Tracks progress against `ARCHITECTURE.md`'s Definition of Done. See that file for full scope/rationale.

## Planned Schedule (week of 2026-09-01)

- **Tue 09-01** — no dev; job-application backlog + interviews (missed apps from weekend travel)
- **Wed 09-02** — PCP appointment (5 hrs, bus/train/foot); optional — #1 and/or #3 if energy allows after
- **Thu 09-03** — no interviews scheduled; focused block — #2, or #1/#3 if Wed didn't happen
- **Fri 09-04** — no interviews scheduled; focused block — remainder of #1/#2/#3
- **Weekend** — applications + Claude Console for app validation/help
- **Week of 09-08** — #4, #5
- **Early week of 09-15** — initial unit-test setup

## 1. Policy Domain Model (DoD #2)

- [x] `Policy` base type + `HomePolicy`/`AutoPolicy` shapes in `core-domain`
- [x] Resolve open question: separate `policies` collection vs. embedded under `clients` — separate `policies` collection, referenced from client by `clientId` (confirmed 2026-09-08)
- [x] Mongo repository + `PolicyService` (create/fetch)
- [x] REST endpoints in `core-api`
- [x] Internal MCP tools (`createPolicy`/`fetchPolicy`)

## 2. Auth Backbone (DoD #1)

- [x] Design pass: per-action-ID + signature mechanism (issuance, storage, verification) — see ARCHITECTURE.md's resolved Open Question (2026-09-08)
- [x] Spring Security filter chain + jjwt signing/parsing
- [x] BCrypt + Mongo-backed user store
- [x] Register/login endpoints (+ `/refresh`, + admin deactivate/reactivate/override-edit endpoints)
- [x] Per-action-ID + signature enforcement on protected endpoints (`@PreAuthorize` wired onto Client/Policy/User-admin endpoints)
- [x] Verify end-to-end against real Mongo — Atlas cluster was paused, resumed 2026-09-08. Full smoke test passed: register/login/refresh, 401 on missing/invalid auth, 403 reserved for authorized-but-wrong-action, tokenVersion forced-refresh (old token dies everywhere incl. `/refresh` itself, not self-healing), last-active-user deactivation guard (409). Two real bugs found and fixed during testing: (1) `ResponseStatusException`'s internal `/error` forward wasn't in the security matcher, silently downgrading intended 401s to empty 403s — fixed via `SecurityConfig`'s `/error` permitAll + `HttpStatusEntryPoint(401)`; (2) `UserAdminController` responses leaked the BCrypt `passwordHash` in plain JSON — fixed via `@JsonIgnore` on `User.getPasswordHash()`.

## 3. Kafka Backbone (DoD #3)

- [x] docker-compose for local Kafka — single KRaft broker (`apache/kafka:3.8.0`, no Zookeeper, no Confluent layer), `kafka-topic-init` one-shot service creates both topics
- [x] Ingestion-routing topic (`ingestion-routing`, 1 partition)
- [x] Lifecycle-events topic (`mcp-lifecycle-events`, 1 partition)
- [x] Throwaway consumer on each topic to prove wiring — verified live 2026-09-08: produced a message on each topic via `kafka-console-producer`, confirmed `core-api`'s `@KafkaListener`s logged receipt on both

## 4. `ollama-mcp-server` (DoD #4)

- [x] Module scaffold (mirrors `internal-mcp-server`) — installed Ollama runtime via winget + pulled `llama3.2` (2026-09-08, not present before this session)
- [x] Lifecycle self-registration (start/heartbeat/shutdown → Kafka) — verified live: register + heartbeat events observed on `mcp-lifecycle-events`
- [x] One real ETL workflow against digital input — verified live: freeform intake text → Ollama structured extraction → real `Client` persisted via `ClientService`, all fields correct (name/DOB/phone/email). Known limitation, not fixed (belongs to Sprint 4's confidence-validation work per ARCHITECTURE.md): a garbage/off-topic input message caused Ollama to hallucinate a plausible-looking fake client rather than fail cleanly — Sprint 2's workflow assumes clean, already-digital, linear input, not adversarial-input handling.
- Note: dropped an initially-added MCP-tool exposure of the same extraction workflow (`ClientExtractionToolset`) — it created a real circular dependency (Spring AI's `ChatClient.Builder` auto-config enumerates every `ToolCallbackProvider` bean as a callable tool for the model itself, including the tool wrapping the service that needs the same `ChatClient.Builder`). Kafka-triggered path only for now; on-demand MCP exposure is a later-sprint revisit.

## 5. Frontend Shell (DoD #5–6)

- [x] `frontend/` workspace scaffold (npm workspaces: `@mosaic/shared` — API client, Zustand auth store, types — plus `@mosaic/web`, a Vite/React/TS app; lives outside the Maven reactor)
- [x] Left nav (Yearly-Yields-styled, hamburger fallback) — three responsive tiers: full labeled sidenav (desktop) → icon-only 64px rail (≤768px) → hidden behind a hamburger toggle (≤480px), ported from `dashboard-shell` in the Yearly-Yields Angular app (220px width, 64px toolbar, accent border pattern)
- [x] "Dashboard coming soon" placeholder route
- [x] Login flow wired to real auth — added CORS config to `core-api`'s `SecurityConfig` (missing until now; needed for any browser-based frontend to call the API at all). Verified live 2026-09-08: CORS preflight + actual login POST from the Vite origin both succeed end-to-end, full module graph (including the `@mosaic/shared` workspace package) transpiles/serves with no errors. Not visually confirmed in an actual browser — no browser/screenshot tool available in this environment; verified via direct HTTP checks of every layer that could silently break instead (module resolution, CORS headers, the auth request itself).

## 6. Initial Unit Testing (early week of 09-15)

- [x] Test setup/tooling decision — backend: JUnit 5 + Mockito + AssertJ (already on the classpath via `spring-boot-starter-test`, no new dependency needed). Frontend: Vitest, added to `@mosaic/shared` (plain-Node tests, no jsdom — keeps the package platform-agnostic for a future React Native island).
- [x] `core-domain`: `ClientCompletenessServiceTest` (9 tests — Sprint 1 logic that shipped with zero coverage until now) and `UserServiceTest` (12 tests — the auth backbone's core logic: `resolveActions`, `updateActionOverrides`' tokenVersion bump, `deactivate`'s last-active-user guard, `reactivate`)
- [x] `core-api`: `JwtServiceTest` (3 tests — issue/parse round trip, foreign-signature rejection, expiry)
- [x] `ollama-mcp-server`: `ClientExtractionServiceTest` (2 tests — mocks the `ChatClient` fluent chain to test field-mapping logic without a live Ollama instance, including that a bad `dateOfBirth` fails loudly rather than persisting garbage)
- [x] `@mosaic/shared`: `authStore.test.ts` (5 tests — login/logout/refresh state transitions)
- All 30 tests passing (25 backend + 5 frontend) as of 2026-09-08. Not yet covered: controller-level/integration tests (`AuthController`, `JwtAuthenticationFilter`, `SecurityConfig` wiring), `PolicyService`/`RoleService` (thin CRUD wrappers, lower value), and any React component tests for `@mosaic/web` — reasonable next increment, not blocking.
