export type ApiResult<T> = {
  success: boolean
  message: string | null
  data: T
}

import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig } from 'axios'

const client: AxiosInstance = axios.create({
  baseURL: '',
  timeout: 60000
})

const LS_TOKEN = 'mas_token'

function normalizeApiErrorMessage(message: string | null | undefined): string {
  const raw = String(message || '').trim()
  if (!raw) return 'request failed'
  if (
    /额度不足|金额不足|请充值|remain_money|请求频繁|QPS超过上限|限额|20001|10002/i.test(raw)
  ) {
    return '达到系统请求限额了'
  }
  return raw
}

client.interceptors.request.use((config) => {
  try {
    const url = String(config.url || '')
    if (url.startsWith('/api/ai/') || url.includes('Stream') || url.includes('/api/portal/summarize')) {
      ;(config as any).timeout = 0
    }
    const token = localStorage.getItem(LS_TOKEN)
    if (token) {
      config.headers = config.headers || {}
      ;(config.headers as any).Authorization = `Bearer ${token}`
    }
  } catch {}
  return config
})

client.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err?.response?.status
    if (status === 401) {
      try {
        window.dispatchEvent(new CustomEvent('auth:required'))
      } catch {}
    }
    const msg = err?.response?.data?.message || err?.message
    return Promise.reject(new Error(normalizeApiErrorMessage(msg)))
  }
)

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
  if (!json.success) throw new Error(normalizeApiErrorMessage(json.message))
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

export async function apiPutJson<T, B = unknown>(path: string, body: B): Promise<T> {
  return requestJson<T>(path, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    data: body
  })
}

export async function apiDelete<T>(path: string): Promise<T> {
  return requestJson<T>(path, { method: 'DELETE' })
}

export async function apiGetBlob(path: string): Promise<Blob> {
  const res = await client.request<Blob>({
    url: path,
    method: 'GET',
    responseType: 'blob'
  })
  const blob = res.data
  if (blob.type && blob.type.includes('application/json')) {
    const text = await blob.text()
    try {
      const j = JSON.parse(text) as { message?: string }
      throw new Error(j.message || 'request failed')
    } catch (e: any) {
      if (e instanceof SyntaxError) throw new Error(text || 'request failed')
      throw e
    }
  }
  return blob
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

