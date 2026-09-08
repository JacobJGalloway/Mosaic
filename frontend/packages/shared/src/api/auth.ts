import { getApiBaseUrl } from '../config';
import type { AuthResponse, LoginRequest, RegisterRequest, ApiError } from '../types';

async function handle(response: Response): Promise<AuthResponse> {
  if (!response.ok) {
    const error: ApiError = { status: response.status, message: await response.text() };
    throw error;
  }
  return response.json();
}

export async function login(request: LoginRequest): Promise<AuthResponse> {
  const response = await fetch(`${getApiBaseUrl()}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  return handle(response);
}

export async function register(request: RegisterRequest): Promise<AuthResponse> {
  const response = await fetch(`${getApiBaseUrl()}/api/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  return handle(response);
}

export async function refresh(currentToken: string): Promise<AuthResponse> {
  const response = await fetch(`${getApiBaseUrl()}/api/auth/refresh`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${currentToken}` },
  });
  return handle(response);
}
