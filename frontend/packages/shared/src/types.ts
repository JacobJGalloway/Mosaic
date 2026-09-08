// Mirrors core-api's auth DTOs (com.mosaic.api.auth.*). Kept minimal and
// hand-synced for now — Sprint 2 scope doesn't include codegen from the
// backend's OpenAPI/schema.

export interface AuthResponse {
  token: string;
  userId: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  roleId: string;
}

export interface ApiError {
  status: number;
  message: string;
}
