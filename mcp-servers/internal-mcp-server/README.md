# internal-mcp-server

Exposes `core-domain` service methods as MCP tools over stdio transport (Spring AI's `spring-ai-starter-mcp-server`). Trusted-internal entry point — not the auth-gated REST surface `core-api` provides; anything with stdio access to this process can invoke its tools directly.

## Tools exposed

- **`ClientToolset`** — `createClient`, `fetchClient`, `checkClientCompleteness`
- **`PolicyToolset`** — `createPolicy`, `fetchPolicy`, `fetchPoliciesForClient`

Both are registered as `MethodToolCallbackProvider` beans in `InternalMcpServerApplication`.

## Running it

```
. ..\..\scripts\load-env.ps1
mvn org.springframework.boot:spring-boot-maven-plugin:3.3.4:run
```
(run `load-env.ps1` from the repo root path, in whichever PowerShell session you'll run `mvn` in — it's per-session, not persistent. Then `mvn ...` from inside `mcp-servers/internal-mcp-server/`). Needs `MONGODB_URI` set. Since transport is stdio, `spring.main.banner-mode` and console logging are both suppressed in `application.yml` — anything printed to stdout would corrupt the MCP protocol stream. Logs go to `./logs/internal-mcp-server.log` instead.

## Notes

`@EnableMongoRepositories(basePackages = "com.mosaic.domain")` — this used to be scoped to `com.mosaic.domain.client` only, which silently meant `PolicyRepository` was never picked up (found and fixed during Sprint 2's Policy work). Keep this at the `com.mosaic.domain` level as new repository packages get added under `core-domain`.
