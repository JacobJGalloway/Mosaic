# Mosaic — Sprint 2 Architecture (Auth, Domain Expansion, Digital-Data Ingestion, UI Shell)

Scope note: this document covers Sprint 2 only. Bulk-scan OCR, the Claude MCP, the orchestrator/LangGraph4j, and Redis/vector search are later sprints and are intentionally not designed here beyond the extension points noted. See `OVERVIEW.md` → Roadmap for full cross-sprint sequencing. Sprint 1's architecture is archived at `docs/archive/ARCHITECTURE-sprint1.md`.

## Sprint 2 Goal

Take the client-only Sprint 1 slice and turn it into an authenticated, policy-aware system fed by a real (if narrowly-scoped) ingestion backbone, with a UI shell a user can actually log into. Concretely: hand-rolled JWT auth, home/auto policy domain models layered on the client record, a Kafka backbone sized for digital-only data, an Ollama MCP server for linear/deterministic digital-data workflows, and a React UI shell (nav + login + placeholder dashboard).

## Definition of Done

Sprint 2 is done when all of the following hold:

1. A user can register/log in and receive a JWT; protected `core-api` endpoints reject requests without a valid token + per-action ID + signature (per the mitigation design below), and accept them with one.
2. Home and auto policy records can be created and fetched via REST and via the internal MCP server, associated to an existing client record, without any migration of the Sprint 1 client schema.
3. A Kafka backbone is running (locally, via docker-compose or equivalent) with topics for (a) digital-data ingestion routing and (b) MCP-server lifecycle events (register/heartbeat/shutdown), and at least one consumer on each topic.
4. An `ollama-mcp-server` module exists, self-registers on the lifecycle-events topic, and can execute at least one real ETL post-processing workflow end-to-end against already-digital input (no OCR-confidence branching required this sprint).
5. A React UI shell is deployed locally: left-side nav (Yearly-Yields-styled, responsive-first with hamburger fallback), a working login flow against the new auth, and a "dashboard coming soon" placeholder route.
6. Frontend lives as its own workspace (`frontend/`) alongside, not inside, the Maven reactor, with a shared library (API client, state, types) plus a `web` app package.
7. Everything above is demonstrable via direct HTTP/UI walkthrough, mirroring how Sprint 1 was verified.

## Explicitly Not Sprint 2

- Bulk-scan OCR / document-mapping pipeline (Sprint 3) — Sprint 2's ingestion path only handles data that's already digital.
- Claude MCP, the orchestrator, LangGraph4j (Sprint 3+) — no multi-agent coordination this sprint.
- True gap-analysis / confidence-based completeness upgrade for client or policy records (Sprint 4) — Sprint 1's rules-based `ClientCompletenessService` is not being replaced yet; a policy-level equivalent, if added, stays equally temporary.
- Redis / vector store (not needed until the Compacted RAG reporting tool is built, Sprint 3+).
- Read-only client/policy views beyond the placeholder dashboard route (Sprint 3).
- Mobile (React Native) — the frontend workspace structure leaves room for it, but no mobile app is built this sprint.
- Any OCR-confidence-based routing logic in the Ollama MCP — that ambiguity is explicitly deferred to Sprint 3's bulk-scan pipeline.

## Service Boundaries

- **Java / Spring Boot — core API service (`core-api`).** Existing Sprint 1 module. Gains: Spring Security filter chain, JWT issuance/validation endpoints, and REST endpoints for home/auto policy create/fetch, layered on `core-domain`.
- **`core-domain`.** Existing module. Gains: `Policy` domain types (home/auto as distinct shapes sharing a common base, per `OVERVIEW.md` → Domain Scope's RV-extensibility note), a Mongo-backed `User`/credentials store, and BCrypt hashing for password storage.
- **Internal MCP server (`internal-mcp-server`).** Existing module. Gains: `createPolicy`/`fetchPolicy` tools mirroring the existing client tools, backed by the same `core-domain` service layer.
- **`ollama-mcp-server` (new module).** New MCP server, scoped narrowly to already-digital-data ingestion — agent-entered or pre-digitized data run through linear/deterministic workflows, no OCR-confidence ambiguity. Self-registers on startup and publishes heartbeat/shutdown events to the Kafka lifecycle-events topic, per `OVERVIEW.md`'s Kafka design.
- **Kafka (new).** Plain Apache Kafka, no Confluent layer. Two topics this sprint: a digital-data ingestion/routing topic, and an MCP-server lifecycle-events topic. Sized for Sprint 2's narrower digital-only scope — the full bulk-scan pipeline is Sprint 3.
- **MongoDB Atlas.** Existing store. Gains: `policies` collection (or embedded under `clients`, TBD — see Open Questions), a `users` collection for auth.
- **Auth (new, in `core-api`).** Hand-rolled JWT — Spring Security for the filter chain, jjwt (or equivalent) for signing/parsing, BCrypt for hashing, Mongo-backed user store. Chosen over an external identity provider given project scale/budget and to demonstrate the implementation directly.
- **Frontend workspace (`frontend/`, new, outside the Maven reactor).** npm/yarn-workspaces monorepo, structured as "islands" mirroring the MCP-server convention: a shared library (API client, shared state, shared types) plus a `web` app (React, Zustand). React Native is a later, not-yet-built island.

## Auth Design — Token Storage Mitigation

Web stores the JWT in `localStorage` (mobile, when built, uses native secure storage instead). This is a deliberate, previously-patented and independently pentested (two weeks, no success) design: possessing the token alone is not sufficient to act maliciously. Every protected action additionally requires a specific per-action ID tied to that action's permissions, plus an active signature — so `localStorage`'s XSS-readability is mitigated by requiring more than token possession, not overlooked. This sprint's DoD item 1 covers implementing this three-part check (token + per-action ID + signature), not just plain JWT validation.

## Data Flow (Sprint 2)

1. A user logs in via the React `web` app; `core-api` validates credentials against the Mongo-backed user store and issues a JWT.
2. Protected requests to `core-api` (REST) carry the JWT, a per-action ID, and a signature; the filter chain validates all three before the request reaches `core-domain`.
3. Already-digital client/policy data arrives via REST or MCP tool calls (internal or Ollama-facing), same dual-path pattern as Sprint 1.
4. Digital-data ingestion events and MCP lifecycle events publish to their respective Kafka topics; the Ollama MCP consumes ingestion events for ETL post-processing and publishes its own lifecycle events on start/heartbeat/shutdown.
5. Policy records persist to MongoDB alongside the existing client record, associated by client ID.

No OCR, no confidence-based routing, no Claude MCP, no orchestrator exists in this data flow — every field still arrives already structured, same as Sprint 1, just with more shapes (policy) and more paths (Kafka, Ollama MCP) in play.

## Open Implementation Questions for Sprint 2

- ~~Policy storage shape~~ — resolved 2026-09-08: `policies` is its own Mongo collection, referenced from `clients` by `clientId` (not embedded), for independent lifecycle/query needs.
- ~~Per-action ID issuance/lookup mechanism~~ — resolved 2026-09-08. `Role { id, name, actionIds }` holds a baseline permission set; `User { id, roleId, actionOverridesAdd, actionOverridesRemove, tokenVersion, active }` layers per-user grants/revocations on top. At login/refresh, `resolved = role.actionIds ∪ user.actionOverridesAdd − user.actionOverridesRemove` is computed fresh and baked into the JWT's `actions` claim (never the role name) — that's the "per-action ID" piece. The "signature" piece is the JWT's own cryptographic signature (jjwt): it's what makes the `actions` claim tamper-evident, not a second secret. Each protected request is checked in this order: (1) JWT signature + expiry valid, (2) the token's `tokenVersion` claim matches the user's current stored `tokenVersion`, (3) the endpoint's required action ID is present in the token's `actions` claim. A short JWT TTL (proposed 30 min) keeps ordinary role/override drift (promotions, demotions, training completions) stale for only a small window until the next natural refresh. For destructive/security-relevant changes (termination, permission edits — including a super-admin editing their own permissions, or self-deactivation), the service layer bumps `tokenVersion` server-side for every user whose resolved actions changed as part of that operation — which naturally includes the actor's own token when they're editing themselves, with no separate blanket rule needed — invalidating any of their currently-held tokens on the very next request rather than waiting for expiry. User deactivation is a soft delete (`active=false`, reversible via reactivation) guarded by an invariant: the last remaining active user in the system can never be deactivated, since there would be no one left able to reactivate anyone.
- Kafka topic partitioning/consumer-group sizing — Sprint 2 traffic is expected to be low-volume, so default single-partition topics are likely sufficient, but not yet confirmed against the Ollama MCP's expected throughput.
- Exact home/auto `Policy` field set — needs its own mini gap-analysis pass against real intake requirements before implementation, similar to how the client completeness schema was derived in Sprint 1.
- Future (not Sprint 2): `Role`/`User` Mongo `_id`s are only unique within a single database today. If Mosaic ever moves to a shared multi-tenant database rather than one database per client organization, these will need a tenant/client-org key folded into the ID (or a compound uniqueness constraint) to stay system-level unique. Not a problem while each client org gets its own database/connection string, which is the current assumption — flagged by Jacob 2026-09-08 as worth watching, not deciding now.
