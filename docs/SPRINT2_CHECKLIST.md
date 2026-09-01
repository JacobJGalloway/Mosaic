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

- [ ] `Policy` base type + `HomePolicy`/`AutoPolicy` shapes in `core-domain`
- [ ] Resolve open question: separate `policies` collection vs. embedded under `clients`
- [ ] Mongo repository + `PolicyService` (create/fetch)
- [ ] REST endpoints in `core-api`
- [ ] Internal MCP tools (`createPolicy`/`fetchPolicy`)

## 2. Auth Backbone (DoD #1)

- [ ] Design pass: per-action-ID + signature mechanism (issuance, storage, verification)
- [ ] Spring Security filter chain + jjwt signing/parsing
- [ ] BCrypt + Mongo-backed user store
- [ ] Register/login endpoints
- [ ] Per-action-ID + signature enforcement on protected endpoints

## 3. Kafka Backbone (DoD #3)

- [ ] docker-compose for local Kafka
- [ ] Ingestion-routing topic
- [ ] Lifecycle-events topic
- [ ] Throwaway consumer on each topic to prove wiring

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
