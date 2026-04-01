import { apiPostJson } from './http'

export type LoginRequest = { username: string; password: string }
export type LoginResult = { token: string; userId: number; username: string; nickname: string | null }
export type LogoutRequest = { token: string }

export async function login(body: LoginRequest) {
  return apiPostJson<LoginResult, LoginRequest>('/api/auth/login', body)
}

export async function logout(body: LogoutRequest) {
  return apiPostJson<void, LogoutRequest>('/api/auth/logout', body)
}

