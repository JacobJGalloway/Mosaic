import { getApiBaseUrl } from '../config';
import { useAuthStore } from '../state/authStore';
import type { ApiError } from '../types';

/**
 * Generic authenticated request helper for endpoints beyond auth itself
 * (Client/Policy, once the dashboard reads/writes them in a later
 * sprint). Attaches the current token; does not itself retry on 401 —
 * callers decide whether to call refresh() and retry.
 */
export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = useAuthStore.getState().token;
  const headers = new Headers(options.headers);
  headers.set('Content-Type', 'application/json');
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(`${getApiBaseUrl()}${path}`, { ...options, headers });
  if (!response.ok) {
    const error: ApiError = { status: response.status, message: await response.text() };
    throw error;
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return response.json();
}
