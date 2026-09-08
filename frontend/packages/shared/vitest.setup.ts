// Runs before any test module imports, so zustand's persist middleware
// (which reads localStorage during store creation) never hits a bare
// ReferenceError in plain Node. Deliberately minimal rather than jsdom —
// this package stays platform-agnostic (a future React Native island
// won't have window.localStorage either).
const store = new Map<string, string>();

const memoryLocalStorage = {
  getItem: (key: string) => store.get(key) ?? null,
  setItem: (key: string, value: string) => store.set(key, value),
  removeItem: (key: string) => store.delete(key),
  clear: () => store.clear(),
};

// zustand's default storage resolves window.localStorage, not a bare
// localStorage global — Node has neither, so both need defining.
(globalThis as any).localStorage = memoryLocalStorage;
(globalThis as any).window = { localStorage: memoryLocalStorage };
