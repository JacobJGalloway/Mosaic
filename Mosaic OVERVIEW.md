# Mosaic — Project Overview

## Problem Statement

Insurance companies face growing pressure to deliver faster and more accurate services. Many are still weighed down by manual document handling — claims, policy applications, and compliance files that take hours or even days to process. This slows operations, increases costs, and risks frustrating customers who expect instant digital experiences.

Manual workflows are not only slow but also error-prone and risky. A single misplaced or misread document can lead to compliance breaches or incorrect payouts, causing financial and reputational damage. Competitors who adopt automation are already processing claims in hours rather than days, setting higher expectations for customer service.

## Solution Summary

Mosaic uses AI-first integration workflows to piece together the fragments of these processes and re-represent a streamlined workflow back to the end user, ready for analysis, review, and submission as needed. A secondary advantage of Mosaic's document-processing approach is market reach: by supporting non-electronic and undocumented form sources (not just modern electronic intake), Mosaic can onboard insurers whose partner departments — government or otherwise — aren't yet electronic-form-ready.

Every architectural decision in Mosaic is deliberately scoped, built where it's genuinely useful now. Growth is designed to be cheap later, and features stop before speculative complexity outruns actual demand. This in turn gives managed, scoped sprint work a more natural feel, as no feature gets a massive start. It gets a minimal start with intentional openings for extension into possible future growth.

## Domain Scope

Mosaic starts in personal lines insurance — home and auto — with the domain model deliberately structured so recreational vehicles (treated as their own regulatory category in most states) can be added as a third policy shape without re-architecting the client or policy layers.

Regulatory and jurisdictional scope for the MVP is intentionally narrow: Illinois and neighboring states with materially similar requirements. Broader multi-state expansion — including states with meaningfully divergent rules, such as Texas — is a known, deliberately deferred extension rather than an oversight.

## System Components

- **MongoDB Atlas** — cloud-hosted NoSQL primary data store. PostgreSQL is a fallback if NoSQL structuring becomes unwieldy, possibly holding a master record keyed to the case document ID.
- **Java / Spring** — handles business and document update logic, direct validation of a given update, and serves as the main interface service to the frontend. Also handles general API/UI orchestration. (Standing in for a decade-plus-dormant .NET microservices background — deliberately chosen to rebuild proficiency in the stack.)
- **Ollama** — AI model being used for this project alongside Claude, chosen for high-throughput, low-latency, lower-complexity workloads (ETL post-processing, OCR-mapping routing).
- **Kafka** — plain Apache Kafka (no Confluent layer) handling two responsibilities: (1) the document-ingestion pipeline — bulk scan intake, OCR/confidence-based routing to the human-in-the-loop validation queue — and (2) MCP-server lifecycle events, where MCP servers self-register on startup and publish heartbeat/shutdown events to a lifecycle-events topic. The orchestrator consumes this topic to maintain a live view of available agents rather than relying on static configuration. This same event stream is designed to also feed an internal admin status view (and could extend to a public-style status page later, with minimal additional work, if Mosaic ever has external customers). If the origin is the API, the API returns a 202 to note the request was accepted and in progress.
- **Redis (chunking and vector search)** — a vector store used to make contextual searches based on embeddings, supporting the deep reasoning/reporting tool's Compacted RAG process. Not required until that component is implemented.
- **LangGraph4j** — Java-native agent orchestration layer, integrating directly with Spring AI (no separate Python bridge service required). Provides stateful, cyclical graph coordination between the orchestrator and specialist agents — necessary once more than one AI role is active, since the gap-analysis flow may need to re-invoke a specialist agent based on what a later step finds. Also designed to support dynamically registering new MCP servers and adjusting agent counts to handle load, without requiring a restart.
- **MCP Servers** — orchestrate specifically between AI model agents and internal business logic, all exposed via the same protocol. This includes an internal MCP server exposing Java methods as tools, an Ollama-facing MCP, and a Claude-facing MCP. Java/Spring acts as the entry point that triggers tool discovery and agent workflows via the orchestrator.
- **React / Zustand** — progressive static web pages, including wizards that direct the end user through needed case gap-fill processes. Zustand chosen over Redux for lighter-weight state/scope management.

## AI Layer — Design Rationale

- Regulatory frameworks vary by state and coverage line — a RAG model grounded in the relevant compliance documents can reason across that complexity without hardcoding rules that change.
- The validation is explainable — RAG gives citations back to source material, so when a compliance flag fires you can show exactly which regulation was triggered and why, not just "this failed a rule."
- Iterative validation catches drift — a case that was compliant at intake can drift out of compliance as coverage details, dates, or claimant information gets updated through subsequent wizards.
- Claude's presence is appreciated but not architecturally required. Only the tool contract is required — if the Claude subscription lapses and isn't renewed, the deep reasoning/reporting tool can be re-implemented against the internal MCP or the Ollama MCP. The system is designed to remain fully functional in a Claude-degraded or Claude-absent state.

## AI Model Roles & Orchestration

**OCR / document mapping (non-AI, internal).** Handles undocumented or non-electronic forms — e.g., a government department that can only hand off a scanned image rather than a structured electronic form. Uses a deterministic OCR process (Tesseract-style) to go from image to text. Each document type is mapped once — relational coordinates and/or form field mappings to domain model properties — then referenced by an ETL adapter/engine that translates the source document into Mosaic's domain model format for processing and persistence. Incoming documents are checked against known mappings first: if a match exists, the document routes straight to ETL; only unmatched document types trigger a new mapping pass. This is treated as a document/data process, not part of the AI reasoning layer.

**ETL post-processor (Ollama).** Takes ETL output and structures domain data for persistence into MongoDB Atlas. Chosen for high throughput, low latency, and low complexity relative to the reasoning workloads below. Each OCR read carries a configurable confidence threshold; reads that fall below it, along with the source scan, route to a human-in-the-loop validation and update queue rather than being auto-accepted.

**Discovery / wizard tool (Claude — scoped for initial implementation).** Originally scoped to handle two cases: resolving low-confidence or hard-to-read document extractions, and capturing data when no source document exists at all. For initial implementation, scope is narrowed to the missing-document case only — walking the client user through providing information directly when there's nothing to scan. This significantly shortens time-to-deliverable and simplifies the initial build. The hard-to-read-document resolution case remains on the roadmap, to be reintroduced once the missing-document flow is proven out. Both interim resolution paths — the low-confidence OCR review and the missing-document capture — are implemented as structured UI/human-in-the-loop workflows initially, with no AI reasoning required:
  - A side-by-side scan-and-form view lets a reviewer correct low-confidence OCR extractions directly.
  - A structured form captures data when no source document exists.
  - Both are candidates for future AI-assisted upgrades (suggested corrections, conversational capture) once the underlying document-mapping and client-completeness flows are proven out.

**Deep reasoning / reporting tool (Claude).** Handles complex analysis of domain data and dynamic, on-demand report generation via a Compacted RAG model, following the approach used on Yearly Yields. *(Documentation for this component to be finalized once implementation begins.)*

**Orchestration boundary.** Java/Spring owns API/UI management and general business orchestration. LangGraph4j (via the orchestrator) owns orchestration between AI agents and workflows specifically — Java/Spring is the entry point into it for tool discovery and workflow triggering. This includes MCP servers exposing internal Java methods as AI tools, alongside the Ollama-facing and Claude-facing MCP servers.

## Client Completeness

Before any policy-specific (home/auto) processing begins, a client record must satisfy a client-level completeness schema — identity, contact information, prior insurance/continuous coverage history, household composition, and core identity/address-verification documents. This check is intentionally policy-agnostic: it gates entry into policy-specific gap analysis rather than being solved simultaneously with it. The required-document list is expected to grow as specific document types (e.g., mortgage, deed, trust-transfer documents) are identified and added.

## AI Separation of Concerns

Different AI models are purposed for their individual strengths — OCR/ETL structuring, high-throughput data processing, and deep regulatory reasoning — so the AI layer stays functional, validated, and each piece does what it's actually good at.

Beyond task-splitting, Mosaic is also meant to lay groundwork for a broader question: whether using multiple specialized AI models together can elevate the system's overall capability more than any one model could achieve in isolation. This is an active research goal, not a defined MVP feature. It starts as a feasibility study — investigating whether learning from one LLM can transfer into another's training data, or whether models can share a central, continuously updated training data source — with implementation to follow only if the study supports it.

## MVP Scope

Sprint 1 (see Roadmap) — client creation only: internal MCP + Ollama MCP assembling a composite client record, validated against the client completeness schema via a temporary rules-based check. No Claude MCP, no orchestrator/LangGraph4j in Sprint 1, since only one AI role is active. The AI Separation of Concerns research track is explicitly excluded from all MVP and near-term sprint scope.

## Roadmap

**Sprint 1 (MVP).** Client creation only. Internal MCP server (structured data pull) and Ollama MCP (bulk document ingestion, OCR/Tesseract mapping, confidence-scored routing to a human-in-the-loop validation queue) assemble a composite client record. A rules-based completeness check validates the record against the client-level schema — a deliberately temporary, non-AI gap check that gets upgraded in Sprint 2. Kafka backbone in place for document ingestion and MCP-server lifecycle events, though dynamic scaling logic is not yet built. No Claude MCP, no orchestrator/LangGraph4j yet.

**Sprint 2.** Introduces the Claude MCP (deep reasoning/reporting tool) and the orchestrator (LangGraph4j), upgrading the Sprint 1 rules-based completeness check into true gap analysis — reasoning about *why* a gap matters and what would resolve it, not just whether a field is populated. Home and/or auto policy shapes get finalized and layered on top of the already-complete client record, since client completeness is a prerequisite gate before policy-specific gap analysis runs. Also completes the discovery/wizard tool's initial scaffolding: the side-by-side scan-and-form review UI and the missing-document capture form, both as human-in-the-loop UI for now. Kafka dynamic/scheduled scaling is a possible stretch addition if it fits.

**Sprint 3 (if not absorbed into Sprint 2).** Kafka consumer-driven dynamic scaling (daytime elastic scaling / nighttime scheduled workflow scaling), if not completed in Sprint 2. Continued build-out of home/auto shapes if not finalized.

**(Possible Sprint 4) Demo Stable State.** A version stable enough to walk someone through live: client creation through completeness validation, low-confidence/missing-document resolution UI, home/auto policy shape with true gap analysis, and (if ready) a generated compliance report via the deep-reasoning tool. Room is deliberately left for a discovery sprint here, consistent with how Switchyard and Yearly Yields surfaced their own mid-build discoveries. This is the checkpoint for evaluating direction and resourcing before committing further, not a finished product.

*Out of scope for all sprints above: multi-state regulatory expansion beyond Illinois and materially similar neighboring states (Texas explicitly excluded as a divergent case), a vision-based claims-intake agent (photo → damage assessment → claim initiation — a natural future extension of the MCP architecture, but requiring computer-vision infrastructure and a hard-approval human gate beyond current scope), and the AI Separation of Concerns research track.*

## Future Direction / Research Track

- Feasibility study: cross-LLM knowledge transfer, or a shared central training data source updated collectively by all models in the system.
- Implementation resolution to follow only if the feasibility study supports it.
- Vision-based claims intake (photo-driven damage assessment and claim initiation) — a distinct, larger future phase, back-pocketed without a committed timeframe.

## Open Questions

- **Data-transmission validation (resolved direction, design pending):** confirmed approach — start small with hashing/HashMap checks for validating "guessed" data against known data, escalate to accepted encryption standards as needed. Build this as a scalable/upgradable adapter layer from the outset, rather than a one-off implementation, since new destinations (government or individual) will require encryption levels not yet supported as they're discovered.
- **MongoDB vs. PostgreSQL fallback trigger:** current document-store implementation approach is expected to hold up well as a read/ingestion source without needing the Postgres fallback. The open piece is the ops side (writes/updates, master record consistency) — likely a later design pass rather than an MVP blocker.
- **API cost/budget line:** neither the OpenAI nor Anthropic APIs have a lasting free tier (only small one-time trial credits); plan for paid usage. For the high-throughput/low-complexity roles (ETL post-processing, OCR-mapping routing), price out smaller/cheaper model tiers rather than full-size models to keep per-call cost down.

## Data Governance Notes

OCR/document mapping is excluded from AI-layer governance concerns since it's an internal deterministic process. The ETL post-processor and the deep reasoning tool handle PII and regulated data, so transmission validation (based on originating source and destination) needs to account for that — see Open Questions above for the encryption/adapter approach.
