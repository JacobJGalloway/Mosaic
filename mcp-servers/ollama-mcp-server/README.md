# ollama-mcp-server

MCP server scoped narrowly to digital-only ingestion — already-digital data run through linear/deterministic workflows, no OCR-confidence branching. Self-registers on the Kafka lifecycle-events topic and runs an Ollama-backed ETL workflow. See ARCHITECTURE.md's Sprint 2 scope for the "why."

## Prerequisites

Needs a running [Ollama](https://ollama.com) instance with at least one model pulled:
```
ollama pull llama3.2
```
On Windows via `winget`: `winget install Ollama.Ollama`. Ollama runs as a local service on `localhost:11434` once installed — no extra step needed to start it.

## Running it

```
mvn org.springframework.boot:spring-boot-maven-plugin:3.3.4:run
```
(from inside `mcp-servers/ollama-mcp-server/`). Needs `MONGODB_URI` set, a reachable Kafka broker, and Ollama running with the configured model pulled.

## Environment variables

| Variable | Default | Purpose |
|---|---|---|
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama API address |
| `OLLAMA_MODEL` | `llama3.2` | Chat model used for extraction |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |

## Lifecycle self-registration (`com.mosaic.mcp.ollama.kafka`)

`LifecycleEventPublisher` publishes to the `mcp-lifecycle-events` topic: `register` on startup (`ApplicationReadyEvent`), `heartbeat` every 30s (`mosaic.mcp.heartbeat-interval-ms`, configurable), `shutdown` on `ContextClosedEvent` (flushed explicitly — the producer would otherwise get torn down before the async send completes).

## ETL workflow (`com.mosaic.mcp.ollama.etl`)

`IngestionRoutingEtlConsumer` consumes the `ingestion-routing` topic. For each message (freeform, already-digital intake text), `ClientExtractionService` uses Ollama (via Spring AI's `ChatClient`, structured output into `ExtractedClientInfo`) to extract identity/contact fields, then persists a real `Client` through `core-domain`'s `ClientService` — the same service layer the REST API and `internal-mcp-server` use.

**Known limitation, not fixed (Sprint 4 territory):** garbage/off-topic input can cause the model to hallucinate a plausible-looking fake client rather than fail cleanly. This workflow assumes clean, already-digital, linear input — adversarial-input handling is explicitly Sprint 4's confidence-validation scope, not this module's.

**Not currently exposed as an on-demand MCP tool** (only the Kafka-triggered path exists) — an earlier attempt at this created a genuine circular dependency: Spring AI's `ChatClient.Builder` auto-configuration enumerates every `ToolCallbackProvider` bean in the context as a tool the model itself can call, which collided with a tool built from the very service that needs that same `ChatClient.Builder`. Revisit once there's a clean way to exclude a tool from the model's own function-calling set.

## Testing

`ClientExtractionServiceTest` (JUnit 5 + Mockito + AssertJ) mocks the full `ChatClient` fluent chain, so it runs without a live Ollama instance. Run with `mvn test -pl mcp-servers/ollama-mcp-server` from the repo root.
