import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import * as authApi from '../api/auth';
import type { ApiError } from '../types';

interface AuthState {
  token: string | null;
  userId: string | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  refresh: () => Promise<void>;
}

// Deliberate design choice, not an oversight — see ARCHITECTURE.md's Auth
// Design: the JWT going in localStorage is mitigated by the per-action-ID
// claim + tokenVersion mechanism enforced server-side, not by hiding the
// token client-side.
export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      token: null,
      userId: null,
      isAuthenticated: false,

      login: async (username: string, password: string) => {
        const response = await authApi.login({ username, password });
        set({ token: response.token, userId: response.userId, isAuthenticated: true });
      },

      logout: () => {
        set({ token: null, userId: null, isAuthenticated: false });
      },

      refresh: async () => {
        const current = get().token;
        if (!current) {
          throw { status: 401, message: 'Not authenticated' } satisfies ApiError;
        }
        const response = await authApi.refresh(current);
        set({ token: response.token, userId: response.userId, isAuthenticated: true });
      },
    }),
    { name: 'mosaic-auth' },
  ),
);
