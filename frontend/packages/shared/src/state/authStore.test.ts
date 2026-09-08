import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { useAuthStore } from './authStore';

describe('useAuthStore', () => {
  beforeEach(() => {
    useAuthStore.setState({ token: null, userId: null, isAuthenticated: false });
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('login stores the returned token and marks the user authenticated', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({ token: 'tok-1', userId: 'user-1' }),
      }),
    );

    await useAuthStore.getState().login('jane', 'secret');

    const state = useAuthStore.getState();
    expect(state.token).toBe('tok-1');
    expect(state.userId).toBe('user-1');
    expect(state.isAuthenticated).toBe(true);
  });

  it('login surfaces a rejected credentials failure without setting state', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: false,
        status: 401,
        text: async () => 'Invalid credentials',
      }),
    );

    await expect(useAuthStore.getState().login('jane', 'wrong')).rejects.toMatchObject({ status: 401 });
    expect(useAuthStore.getState().isAuthenticated).toBe(false);
  });

  it('logout clears token, userId, and authenticated flag', () => {
    useAuthStore.setState({ token: 'tok-1', userId: 'user-1', isAuthenticated: true });

    useAuthStore.getState().logout();

    const state = useAuthStore.getState();
    expect(state.token).toBeNull();
    expect(state.userId).toBeNull();
    expect(state.isAuthenticated).toBe(false);
  });

  it('refresh rejects immediately when there is no current token, without calling the API', async () => {
    const fetchMock = vi.fn();
    vi.stubGlobal('fetch', fetchMock);

    await expect(useAuthStore.getState().refresh()).rejects.toMatchObject({ status: 401 });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it('refresh replaces the token when the API returns a new one', async () => {
    useAuthStore.setState({ token: 'old-tok', userId: 'user-1', isAuthenticated: true });
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({ token: 'new-tok', userId: 'user-1' }),
      }),
    );

    await useAuthStore.getState().refresh();

    expect(useAuthStore.getState().token).toBe('new-tok');
  });
});
