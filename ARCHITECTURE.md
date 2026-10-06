Architecture — Sprint 3 (Branch `dev_3_1`)

Starting Point

Mosaic is a semi-automated, LLM-driven system that sits between an insurer and a claimee. Multi-source ingestion runs automatically. Any tool failure routes to a human review workflow in Mosaic for final resolution, and humans also verify ingested data and monitor report and export output. The rest of the system is expected to operate on its own.

Like the other portfolio projects, Mosaic is demo stable, not production ready. Its data is seeded or filled by a scheduled random generator for demonstration purposes.

Stack: Java / Spring Boot backend, with MCP servers behind the API. MongoDB Atlas is the server-side data store. The UI is a progressive static site using React 19.

MCP servers: Mosaic uses three, each with a distinct role.

Server	Status	Role
Ollama (local LLM)	Running	Small-scale deterministic workflows where high throughput and low latency matter, such as data streams and file I/O
Internal codebase	Running	Fallback for unique workflow points that need a single operation step, which is easier to handle in code than in an LLM
Claude Sonnet 5	Built this sprint	Tools for non-deterministic workflow points. The third and final server

Authentication (pattern shared with the other portfolio projects): user/password login generates a JWT. RBAC permissions are enforced in the UI, API, and service layers. Tokens expire, and are refreshed automatically while the user remains active. A refresh also carries any permission changes made since the last token was issued. Mosaic has no roles defined yet. The eventual boundary is small: users who can handle manual resolutions, and users still in training. Full RBAC enforcement is not expected this sprint, and arrives with the UI and workflows that need it.

Data slices: slices are generated server-side and held only in client memory. Nothing is persisted to browser storage. The domain contract schemas define the fields each slice contains. Slices are kept small and contain only the data needed for the task at hand, which matters most for dashboard visualizations.

Current routing: API endpoints call the MCP servers directly. An orchestrator layer is not present yet. A first orchestrator attempt in Telara was tangled and has since been untangled, which is why Mosaic starts without one. Full tool-discovery and per-request tool-chain building (LangGraph4j-based) is Sprint 4 scope, not this sprint — see Sprint Goal below for what actually ships this sprint.

Design system: built in Claude Design from the structure of an existing validated system, with an emerald palette and a WCAG 2.1 AA target. Palette and typography are committed via DESIGN.md and in use this sprint.

Sprint Goal

Stand up the minimum end-to-end slice: a login, a dashboard with three starter metrics, the third MCP server, and a minimal orchestrator stub.

Spring Boot API scaffold — application class, application.yml with a dev profile, and the base endpoint structure.
Claude Sonnet 5 MCP server — the third and final MCP server, for non-deterministic workflow points. The Ollama and internal codebase servers are reused as-is. (Target add for this sprint.)
Orchestrator stub — a lightweight process that consumes the `mcp-lifecycle-events` topic and reports which MCP servers are currently registered and alive. Structured around the two MCP servers that already exist and doesn't wait on Claude MCP connectivity. This is not the full tool-discovery/tool-chain-building orchestrator — that's LangGraph4j-based, gated on Sprint 4's gap-analysis flow where more than one AI role may need to be re-invoked. The direct API-to-MCP path stays in place this sprint; the orchestrator does not yet sit in the request path. Planned flow below.
Login UI — user/password login against the JWT/refresh backend described above. Full RBAC enforcement is deferred until the workflows that need it exist.
Main dashboard (starter) — the shell plus three metrics, all bucketed by week (a week is a 7-day period regardless of time of day):
Ingestion success rate by week, over the rolling past three months
Total claims resolved by week, over the same rolling three months
Ingestion failure reasons as a percentage breakdown, over the rolling past four weeks
Seed data and generator — seeded history covering the full three months, plus a scheduled random generator that keeps adding data, both stored in MongoDB Atlas, so all three metrics have something to show.
Request Flow

Current:

API endpoints  ->  MCP servers (Ollama, internal codebase)

Planned this sprint:

API endpoints  ->  MCP servers (Ollama, internal codebase, Sonnet 5)   [unchanged — direct path]

Orchestrator stub (side channel, not in the request path):
mcp-lifecycle-events (Kafka)  ->  Orchestrator  ->  live registry of which MCP servers are registered/alive

The orchestrator this sprint does not intercept or route requests. It consumes MCP self-registration/heartbeat/shutdown events and maintains a view of which servers are up — groundwork for Sprint 4, where it takes over actual tool discovery and per-request tool-chain building (LangGraph4j). The direct API-to-MCP path above stays in place and is exercised by all three servers this sprint.

Dashboard Rendering

The UI is a progressive static site. When data is written, the service layer does two things in parallel: it stores the record in MongoDB Atlas, and it runs an ETL/ELT step that uses the domain contract schemas to shape the data into the affected slice. The slice is pushed to the UI over an outbound WebSocket channel on the API layer (Spring WebSocket with STOMP, with the JWT sent on the connect frame). The UI then triggers a partial hydration of only the affected data, and charts re-render asynchronously without a page reload.

The hook lives in the service layer, so later writes, such as manual resolutions, trigger the same push. [Confirm: the push goes out only after the write succeeds.] [Where weekly aggregation happens, in the transform step or in the query behind it: TBD]

Dashboard endpoints return slices: only the aggregated fields each chart needs, never full records. The client holds them in memory only, not in browser storage.

Explicit Non-Goals for This Slice

No human review or failure-resolution workflows — ingestion-failure queues, resolution wizards, and data verification screens are deferred past this sprint
No client-user or policy deactivation, and no supervisor review and pairing for record access removal — these are planned manual-resolution permissions, deferred until roles exist
No full RBAC enforcement — the role boundary (users who can handle manual resolutions versus users still in training) arrives with the resolution workflows that need it
No metrics beyond the three listed above
No record lookup or drill-down screens, and no report or export monitoring - will be scoped for next sprint unless time allows for some addition in this sprint.
No production hardening — Mosaic stays demo stable this sprint

Definition of Done

API and config: the Spring Boot app starts under the dev profile, with configuration externalized and no secrets in the repo.

MCP and orchestrator: each of the three MCP servers is exercised by at least one request over the direct API-to-MCP path. The orchestrator stub consumes `mcp-lifecycle-events` and correctly reports which of the registered MCP servers are currently alive, including a newly registered one, without code changes to the stub. The orchestrator does not sit in the request path this sprint — that's Sprint 4. The write-path push to the UI (see Dashboard Rendering) does not go through the orchestrator.

Login and authorization: a user can log in with a username and password and receive a JWT. The token refreshes while the user stays active, and a refreshed token reflects permission changes. Unauthenticated access to the dashboard is rejected. Full RBAC enforcement is not required this sprint.

Dashboard: all three metrics render from live endpoints, not hardcoded values, over the timeframes above, using seeded and generated data. A data change is pushed over the WebSocket channel and triggers a partial hydration of the affected chart only. Endpoints return slices, and the client holds them in memory only.

Tests: confirm meets coding guidelines by end of sprint

Decisions Made This Sprint

Corrected an earlier scope slip: the full tool-discovery/tool-chain-building orchestrator was briefly planned for this sprint but belongs in Sprint 4 (LangGraph4j, gated on the gap-analysis flow needing to re-invoke specialist agents). Sprint 3 ships the lifecycle-stub orchestrator only, and the direct API-to-MCP path stays live and in the request path this sprint.

Open Risks / Unknowns
The orchestrator's first implementation in Telara went wrong. Keep its interface thin, and keep the direct-path baseline available until the orchestrated path is verified in Sprint 4.
Tool chains, once built in Sprint 4, will be formed per request with the Sonnet 5 server handling non-deterministic work, so results can vary between runs. Tests need to assert on structure and constraints, not on a fixed route or exact output.
Spring Boot is a less familiar framework than the .NET equivalents. Budget extra time for security configuration, which the login UI and the permission-carrying token refresh depend on.

Open Questions
