# core-domain

Domain model and service layer, shared by `core-api` and both MCP servers. This module owns all persistence (`MongoRepository` interfaces) and business logic; it has no web layer of its own — `core-api` is the REST entry point, the MCP servers are the tool-invocation entry points, and both delegate to the services here.

## Packages

- **`com.mosaic.domain.client`** — `Client`, `Identity`, `ContactInfo`, `Address`, `PriorInsuranceRecord`, `HouseholdMember`, `ClientDocument`. `ClientService` handles create/fetch; `ClientRepository` is the Mongo repository (`clients` collection).
  - **`.completeness`** — `ClientCompletenessService`: a deliberately temporary, rules-based (non-AI) check that a client record has the required identity/contact/insurance-history/document fields. Sprint 4 replaces this with true confidence-based gap analysis — see docs/archive/ARCHITECTURE-sprint2.md.

- **`com.mosaic.domain.policy`** — `Policy` (abstract base), `HomePolicy`/`AutoPolicy` (concrete subtypes). Stored in its own `policies` Mongo collection, referenced from a client by `clientId` rather than embedded. `@JsonTypeInfo`/`@JsonSubTypes` on `Policy` let REST/MCP callers POST a bare `Policy` body and have it deserialize to the right subtype (`policyType: "HOME"` or `"AUTO"`) — this is separate from, and in addition to, Spring Data Mongo's own `_class` discriminator used for storage. `PolicyService`/`PolicyRepository` mirror the client pattern.

- **`com.mosaic.domain.auth`** — the JWT/action-ID auth backbone's domain model:
  - `Role { id, name, actionIds }` — a baseline permission set.
  - `User { id, username, passwordHash, roleId, actionOverridesAdd, actionOverridesRemove, tokenVersion, active }` — per-user grants/revocations layered on top of their role.
  - `UserService.resolveActions(user)` computes `role.actionIds ∪ actionOverridesAdd − actionOverridesRemove` fresh on every login/refresh — this resolved set, not the role name, is what ends up in the issued JWT.
  - `UserService.deactivate`/`reactivate` implement the soft-delete + last-active-user guard (the system can never end up with zero active users). Both `updateActionOverrides` and `deactivate` bump `tokenVersion`, which is how a currently-held token gets invalidated immediately instead of waiting for natural expiry — see docs/archive/ARCHITECTURE-sprint2.md's Auth Design for the full mechanism and its rationale.
  - `ActionIds` — the action-ID string constants referenced by `@PreAuthorize` checks in `core-api`.
  - `PasswordEncoderConfig` — defines the `PasswordEncoder` bean here (not in `core-api`) because `UserService` is component-scanned into every app that depends on this module, including `internal-mcp-server`.

## Testing

`ClientCompletenessServiceTest` and `UserServiceTest` (JUnit 5 + Mockito + AssertJ). Run with `mvn test -pl core-domain` from the repo root.

## Notes

- `Client`, `Policy`, and `User`/`Role` are three independent Mongo collections (`clients`, `policies`, `roles`/`users`), not embedded documents — chosen for independent lifecycle/query needs (see docs/archive/ARCHITECTURE-sprint2.md's Open Questions for the Policy-storage decision specifically).
- `Role`/`User` document IDs are only unique within a single database today. If Mosaic ever moves to a shared multi-tenant database (rather than one database per client organization, the current assumption), these will need a tenant key folded in — flagged in docs/archive/ARCHITECTURE-sprint2.md, not yet a problem.
