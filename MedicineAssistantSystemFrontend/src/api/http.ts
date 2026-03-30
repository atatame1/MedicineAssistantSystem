export type ApiResult<T> = {
  success: boolean
  message: string | null
  data: T
}

import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig } from 'axios'

const API_BASE_URL =
  (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080'

const client: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000
})

async function requestJson<T>(
  path: string,
  config: AxiosRequestConfig
): Promise<T> {
  const res = await client.request<ApiResult<T>>({
    url: path,
    ...config
  })
  const json = res.data as ApiResult<T>
  if (!json || typeof json.success !== 'boolean') throw new Error('invalid response')
  if (!json.success) throw new Error(json.message || 'request failed')
  return json.data
}

export async function apiGet<T>(path: string): Promise<T> {
  return requestJson<T>(path, { method: 'GET' })
}

export async function apiPostJson<T, B = unknown>(
  path: string,
  body: B
): Promise<T> {
  return requestJson<T>(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    data: body
  })
}

export async function apiPostMultipart<T, B extends Record<string, any>>(
  path: string,
  form: B
): Promise<T> {
  const fd = new FormData()
  Object.entries(form).forEach(([k, v]) => {
    if (v === undefined || v === null) return
    fd.append(k, v)
  })
  return requestJson<T>(path, {
    method: 'POST',
    data: fd,
  })
}

