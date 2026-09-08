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

- [ ] Module scaffold (mirrors `internal-mcp-server`)
- [ ] Lifecycle self-registration (start/heartbeat/shutdown → Kafka)
- [ ] One real ETL workflow against digital input

## 5. Frontend Shell (DoD #5–6)

- [ ] `frontend/` workspace scaffold (npm/yarn workspaces, shared lib + `web` package)
- [ ] Left nav (Yearly-Yields-styled, hamburger fallback)
- [ ] "Dashboard coming soon" placeholder route
- [ ] Login flow wired to real auth (blocked on #2)

## 6. Initial Unit Testing (early week of 09-15)

- [ ] Test setup/tooling decision — scope TBD when this section starts
