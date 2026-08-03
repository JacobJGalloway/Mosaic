# Mosaic — MVP Architecture (Sprint 1: Client Creation)

Scope note: this document covers Sprint 1 only — client creation, end to end. Home/auto policy shapes, the Claude MCP, the orchestrator/LangGraph4j, and the review/capture UIs are Sprint 2 and are intentionally not designed here beyond the extension points noted. See OVERVIEW.md → Roadmap for full sequencing.

## Sprint 1 Goal

Assemble a composite client record from structured internal data and bulk-ingested documents, and validate it against a client-level completeness schema using a temporary, non-AI rules-based check. No multi-agent coordination is required this sprint, since only one AI role (Ollama) is active.

## Service Boundaries

- **Frontend — React / Zustand.** Client-intake UI: form-based entry for known data, document upload, and a status view of a client record's completeness state (complete / incomplete + which fields are missing). No wizard or review UI yet — those are Sprint 2.
- **Java / Spring Boot — core API service.** Owns the client domain model, request validation, and is the single frontend-facing entry point. Hosts the **internal MCP server**, exposing existing Java business-logic methods (e.g., "create client," "check completeness," "fetch client record") as MCP tools. In Sprint 1, "orchestration" is just Spring Boot calling these tools directly and publishing/consuming Kafka events — there's no LangGraph4j orchestrator yet, since there's only one AI role to coordinate.
- **Ollama MCP server — ingestion/ETL service.** Consumes documents from the Kafka ingestion topic, runs OCR (Tesseract) against known document-type mappings, produces structured field data at a confidence score, and publishes results back to Kafka: high-confidence results go to a "ready for ETL" topic; low-confidence results go to a "needs review" topic (queued for the Sprint 2 review UI — in Sprint 1 this queue can simply persist and remain unresolved until that UI exists, or be manually inspectable via a basic endpoint).
- **Kafka — event backbone.**
  - `document-ingestion` — raw scanned documents entering the pipeline.
  - `document-ocr-results` (or split into `ocr-high-confidence` / `ocr-needs-review`) — OCR output, routed by confidence threshold.
  - `mcp-lifecycle-events` — MCP server startup/heartbeat/shutdown events. In Sprint 1 this can be minimal (just the internal MCP and Ollama MCP publishing on boot) — full self-registration/discovery logic is a Sprint 2+ concern once more servers exist, but the topic and event shape should be established now so nothing has to be retrofitted.
- **MongoDB Atlas — primary data store.** Holds the client record, keyed by a client/case ID. Sprint 1 only needs the client-level schema (see below); policy-specific (home/auto) schemas are added in Sprint 2 without needing a client-record migration, since completeness is checked at the client level first.
- **Rules-based completeness checker.** A straightforward schema-comparison service (can live inside Spring Boot for Sprint 1 — no need for a separate service yet) that checks the assembled client record against the required-fields/documents list and returns what's missing. This is explicitly temporary scaffolding, to be replaced by the Claude + LangGraph4j gap-analysis flow in Sprint 2 — build it knowing it will be swapped out, not extended in place.

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

1. Client-facing form (React) submits known structured data → Spring Boot → persisted as a draft client record in MongoDB.
2. Client uploads documents → API accepts, returns **202** (per OVERVIEW.md convention: accepted, processing in progress) → document published to `document-ingestion`.
3. Ollama MCP consumes, runs OCR against known mappings, scores confidence, publishes result to the appropriate results topic.
4. High-confidence results feed the ETL adapter, which maps fields into the client record's domain shape and updates MongoDB.
5. Low-confidence results are queued (`ocr-needs-review`) — no UI to resolve them yet in Sprint 1; they simply sit in the queue as a known, visible gap.
6. On demand (or on each update), the rules-based completeness checker compares the current record against the schema and reports what's missing.

## What's Explicitly Not in Sprint 1

- Claude MCP, orchestrator, LangGraph4j — no multi-agent coordination needed yet.
- Home/auto policy-specific schemas and gap analysis.
- Low-confidence review UI (side-by-side scan/form) and missing-document capture wizard — the queue exists, but nothing resolves it yet.
- Kafka dynamic/scheduled scaling — the topics and event shape are established now; actual scaling logic (consumer-lag-based or scheduled) is Sprint 2/3.
- Redis/vector store — not needed until the Compacted RAG reporting tool is built.

## Open Implementation Questions for Sprint 1

- Exact Kafka topic/partition naming conventions and whether `document-ocr-results` is one topic with a confidence field or two separate topics — lean toward two, since it simplifies consumer logic (a Sprint 2 review-UI service can just consume the low-confidence topic directly).
- Whether the completeness checker runs synchronously on each record update or as a periodic/event-triggered pass — synchronous is simpler for Sprint 1 and there's no volume yet to justify anything else.
- Confirm MongoDB schema/collection design for the client record before first write (single collection keyed by client ID vs. separate collections for core data vs. documents metadata).
