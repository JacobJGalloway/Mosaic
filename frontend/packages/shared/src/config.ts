// Set once at app startup (web's main.tsx reads its own bundler's env var
// and calls this) rather than read directly here, so this package stays
// bundler-agnostic for a future React Native island.
let apiBaseUrl = 'http://localhost:8080';

export function setApiBaseUrl(url: string): void {
  apiBaseUrl = url;
}

export function getApiBaseUrl(): string {
  return apiBaseUrl;
}
