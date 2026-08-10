# Mosaic — MVP Architecture (Sprint 1: Client Creation)

Scope note: this document covers Sprint 1 only — client creation, end to end, from already-digital sources exclusively. OCR/document ingestion, the Ollama MCP, Kafka, home/auto policy shapes, the Claude MCP, the orchestrator/LangGraph4j, and the UI are all later sprints and are intentionally not designed here beyond the extension points noted. See OVERVIEW.md → Roadmap for full sequencing (OCR/Ollama/Kafka land in Sprint 2, scoped to digital-only ingestion; bulk-scan OCR itself doesn't enter scope until Sprint 3).

## Sprint 1 Goal

Assemble a client record from structured, already-digital data — on-screen forms and internal MCP tool requests only — and validate it against a client-level completeness schema using a temporary, non-AI rules-based check. No document ingestion, no multi-agent coordination; only one deterministic (non-AI) check is in play.

## Service Boundaries

- **Java / Spring Boot — core API service (`core-api`).** Owns the client domain model, request validation, and is the frontend-facing REST entry point (`POST/GET /api/clients`, `GET /api/clients/{id}/completeness`). No UI exists yet to call it — Sprint 1 is verified via direct HTTP calls; the React UI shell arrives in Sprint 2.
- **Internal MCP server (`internal-mcp-server`).** A genuinely separate module/process (stdio transport), not code embedded in `core-api` — imports `core-domain` as a dependency and exposes `createClient`/`fetchClient`/`checkClientCompleteness` as MCP tools via Spring AI's `MethodToolCallbackProvider`. Represents the "internal tool requests" intake path, parallel to the REST path, both backed by the same `core-domain` service layer.
- **MongoDB Atlas — primary data store.** Holds the client record, keyed by a client/case ID, in a single `clients` collection. Sprint 1 only needs the client-level schema (see below); policy-specific (home/auto) schemas are added in Sprint 2 without needing a client-record migration, since completeness is checked at the client level first.
- **Rules-based completeness checker (`ClientCompletenessService`, in `core-domain`).** A straightforward schema-comparison service that checks the assembled client record against the required-fields/documents list and returns what's missing. This is explicitly temporary scaffolding, to be replaced by the Claude + LangGraph4j gap-analysis flow in Sprint 4 — build it knowing it will be swapped out, not extended in place.

## Client Completeness Schema (Sprint 1 target)

Minimum viable field/document set — expected to grow as new document types are identified:

- Identity: full name, date of birth, current address
- Contact information: phone, email
- Prior insurance / continuous coverage history
- Household composition (other drivers/residents, as applicable)
- Identity verification document (e.g., government ID)
- Proof of address document

Extension point: additional document types (mortgage, deed, trust-transfer, etc.) get added to this list as they're identified — no schema redesign needed, just list growth.

## Data Flow (Sprint 1)

1. Known structured client data arrives via one of two equivalent digital paths — a REST call to `core-api`, or an MCP tool call to `internal-mcp-server` — both delegating to the same `ClientService` in `core-domain`.
2. `ClientService.createClient` persists the client record to MongoDB (`clients` collection), stamping `createdAt`/`updatedAt`.
3. On demand, `ClientService.checkCompleteness` (via `ClientCompletenessService`) compares the current record against the client-level schema and reports what's missing.

No document upload, OCR, or ETL path exists in Sprint 1 — every field arrives already structured.

## What's Explicitly Not in Sprint 1

- Any document ingestion at all — no upload endpoint, no OCR, no Ollama MCP, no Kafka. These arrive in Sprint 2 (Ollama MCP + Kafka, scoped to already-digital data only) and Sprint 3 (actual bulk-scan OCR).
- Claude MCP, orchestrator, LangGraph4j — no multi-agent coordination needed yet (Sprint 3+).
- Home/auto policy-specific schemas and gap analysis (Sprint 2 for schemas, Sprint 4 for true gap analysis).
- Any frontend — the React UI shell doesn't start until Sprint 2.
- Auth — no login/JWT yet; Sprint 1's endpoints are unauthenticated (Sprint 2 adds hand-rolled JWT auth).
- Redis/vector store — not needed until the Compacted RAG reporting tool is built.

## Open Implementation Questions for Sprint 1

- Whether the completeness checker runs synchronously on each record update or as a periodic/event-triggered pass — synchronous is simpler for Sprint 1 and there's no volume yet to justify anything else. (Currently implemented as synchronous, on-demand via `GET /api/clients/{id}/completeness`.)
- Confirm MongoDB schema/collection design for the client record before first write (single collection keyed by client ID vs. separate collections for core data vs. documents metadata). (Currently implemented as a single `clients` collection.)
