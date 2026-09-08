# frontend

npm workspace living outside the Maven reactor, structured as "islands" mirroring the MCP-server convention: a shared library plus one implemented project per platform (web now, React Native later if a mobile target is built).

## Packages

- **`@mosaic/shared`** — API client, auth state, and types shared across platforms. Deliberately platform-agnostic (no `window`/DOM assumptions) so a future React Native island can reuse it.
  - `config.ts` — the API base URL is set once at app startup (`setApiBaseUrl`) rather than read directly from a bundler-specific env var here, so this package doesn't assume Vite.
  - `api/auth.ts` / `api/client.ts` — thin `fetch` wrappers. `apiFetch` attaches the current token from `authStore` as a Bearer header.
  - `state/authStore.ts` — Zustand store, persisted to `localStorage` (web). This is a **deliberate** design choice, not an oversight of `localStorage`'s XSS-readability — see ARCHITECTURE.md's Auth Design for the actual mitigation (the per-action-ID claim + `tokenVersion` mechanism enforced server-side).
- **`@mosaic/web`** — the React/TypeScript/Vite app.
  - `shell/AppShell.tsx` — the left nav, three responsive tiers ported from the Yearly-Yields Angular app's `dashboard-shell` layout: full labeled sidenav (desktop) → icon-only 64px rail (≤768px) → hidden behind a hamburger toggle (≤480px, mobile/small tablets).
  - `pages/LoginPage.tsx` — calls `authStore.login`, redirects to `/dashboard` on success.
  - `pages/DashboardPlaceholderPage.tsx` — the Sprint 2 placeholder; real client/policy views land in Sprint 3.
  - `App.tsx` — routing + the `RequireAuth` guard.

## Environment variables

| Variable | Default | Purpose |
|---|---|---|
| `VITE_API_BASE_URL` | `http://localhost:8080` | `core-api`'s address |

Set via a `.env` file in `packages/web/` (Vite convention) if you need to override the default — e.g. pointing at a deployed `core-api` instead of localhost.

## Running it

```
npm install
npm run dev --workspace=@mosaic/web
```
Needs `core-api` running and reachable — `core-api`'s `SecurityConfig` only allows CORS from `MOSAIC_WEB_ORIGIN` (defaults to `http://localhost:5173`, matching Vite's default port).

## Testing

`npm test` (from `frontend/`) runs `@mosaic/shared`'s Vitest suite — currently `authStore.test.ts` (login/logout/refresh state transitions, mocked `fetch`). No React component tests yet for `@mosaic/web` — reasonable next increment, not blocking.
