Mosaic
v 1.1

AI-first integration of data and document ingestion for insurance industry companies with self-analyzing data for alerts and reports based on company policies and regulatory rules related to the company.

## Overview

Insurance companies are often weighed down by manual document handling — claims, policy applications, and compliance files that take hours or days to process. Mosaic uses AI-first integration workflows to piece together the fragments of these processes and re-represent a streamlined workflow back to the end user, ready for analysis, review, and submission. By supporting non-electronic and undocumented form sources — not just modern electronic intake — Mosaic can also onboard insurers whose partner departments (government or otherwise) aren't yet electronic-form-ready.

Mosaic starts in personal lines insurance (home and auto), scoped narrowly to Illinois and neighboring states with similar requirements for its MVP. Every architectural decision is deliberately scoped to what's genuinely useful now, with intentional openings for future growth rather than speculative complexity up front.

For the full problem statement, domain scope, and sprint-by-sprint roadmap, see [OVERVIEW.md](OVERVIEW.md). `ARCHITECTURE.md` is a per-sprint working document rather than a persistent one — it lives on the active development branch during a sprint, then gets archived to `docs/archive/` once that sprint merges to `main` (most recently, [docs/archive/ARCHITECTURE-sprint2.md](docs/archive/ARCHITECTURE-sprint2.md)).

## Tech Stack

- **Backend:** Java 21 / Spring Boot, Maven multi-module reactor
- **Database:** MongoDB Atlas
- **Auth:** Hand-rolled JWT (Spring Security, jjwt, BCrypt)
- **Messaging:** Apache Kafka (plain, no Confluent layer)
- **AI:** Ollama (local, digital-data ETL workflows); Claude (planned, Sprint 3+)
- **MCP:** Spring AI MCP servers — an internal tools server plus an Ollama-facing server
- **Frontend:** React + TypeScript (Vite), Zustand, npm workspaces — lives outside the Maven reactor
- **Local dev infra:** Docker Compose (Kafka)

Each module has its own `README.md` with the technical details specific to it — see the links in Getting Started below.

## Getting Started

### Prerequisites

- JDK 21, Maven
- Node.js + npm
- Docker Desktop (for Kafka)
- A MongoDB Atlas cluster (or compatible MongoDB instance)
- [Ollama](https://ollama.com) installed locally, with at least one model pulled (e.g. `ollama pull llama3.2`) — only needed to run `ollama-mcp-server`

### Environment variables

Create a `.env` file at the repo root (gitignored) with at minimum:

```
MONGODB_URI="mongodb+srv://<user>:<password>@<cluster>.mongodb.net"
```

Other variables have working local defaults and only need overriding for non-default setups — see each module's `README.md` for its full list (`MOSAIC_JWT_SECRET`, `MOSAIC_WEB_ORIGIN`, `KAFKA_BOOTSTRAP_SERVERS`, `OLLAMA_BASE_URL`, `OLLAMA_MODEL`, `VITE_API_BASE_URL`).

### Running it

1. **Kafka** (needed by `core-api`, `internal-mcp-server`, and `ollama-mcp-server`):
   ```
   docker compose up -d
   ```
2. **Backend modules** — build once from the repo root, then run whichever module you need:
   ```
   mvn install -DskipTests
   ```
   - `core-api` — [core-api/README.md](core-api/README.md)
   - `mcp-servers/internal-mcp-server` — [mcp-servers/internal-mcp-server/README.md](mcp-servers/internal-mcp-server/README.md)
   - `mcp-servers/ollama-mcp-server` — [mcp-servers/ollama-mcp-server/README.md](mcp-servers/ollama-mcp-server/README.md)
3. **Frontend** — [frontend/README.md](frontend/README.md)
   ```
   cd frontend && npm install && npm run dev --workspace=@mosaic/web
   ```

### Running tests

- Backend: `mvn test` from the repo root (or `-pl <module>` for one module)
- Frontend: `npm test` from `frontend/`
