import { apiDelete, apiGet, apiPutJson } from './http'

export type AiTaskRequest = { input: string; conversationId?: number | null }
export type AiTaskResponse = { agentCode?: string | null; output: string }

export type AiConversationHistoryItem = {
  type: string | null
  conversationId: number | null
  title: string | null
  isTop: boolean | null
  inputText: string | null
  outputText: string | null
  createTime: string | null
}

export type AiAgentMessageItem = {
  role: string | null
  content: string | null
  createTime: string | null
}

function buildHeaders() {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'text/event-stream',
    'Cache-Control': 'no-cache',
  }
  const t = localStorage.getItem('mas_token')
  if (t) headers.Authorization = `Bearer ${t}`
  return headers
}

function resolveSseUrl(path: string): string {
  const b = (import.meta.env.VITE_API_BASE as string | undefined)?.trim()
  if (b) return `${b.replace(/\/$/, '')}${path}`
  if (import.meta.env.DEV) return `http://localhost:8080${path}`
  return path
}

function parseSseEvents(
  buffer: string,
  onData: (s: string) => void,
  onMeta?: (meta: { conversationId: number }) => void
) {
  const parts = buffer.split('\n\n')
  const last = parts.pop() || ''
  for (const block of parts) {
    let eventName = ''
    const dataLines: string[] = []
    for (const line of block.split('\n')) {
      if (line.startsWith('event:')) {
        eventName = line.slice(6).trim()
      } else if (line.startsWith('data:')) {
        let v = line.slice(5)
        if (v.startsWith(' ')) v = v.slice(1)
        dataLines.push(v)
      }
    }
    const payload = dataLines.join('\n')
    if (eventName === 'meta' && onMeta && payload) {
      try {
        const o = JSON.parse(payload) as { conversationId?: number }
        if (o && typeof o.conversationId === 'number') onMeta({ conversationId: o.conversationId })
      } catch {
        /* ignore */
      }
    } else if (payload !== '') {
      onData(payload)
    }
  }
  return last
}

export async function postAiStreamText(
  path: string,
  body: AiTaskRequest,
  onData: (s: string) => void,
  onMeta?: (meta: { conversationId: number }) => void
) {
  const res = await fetch(resolveSseUrl(path), {
    method: 'POST',
    headers: buildHeaders(),
    body: JSON.stringify(body),
    cache: 'no-store',
  })
  if (!res.ok) throw new Error('request failed')
  const reader = res.body?.getReader()
  if (!reader) throw new Error('no stream')
  const dec = new TextDecoder('utf-8')
  let buffer = ''
  while (true) {
    const { done, value } = await reader.read()
    if (done) {
      if (buffer.trim()) {
        buffer = parseSseEvents(`${buffer}\n\n`, onData, onMeta)
      }
      break
    }
    buffer += dec.decode(value, { stream: true })
    buffer = parseSseEvents(buffer, onData, onMeta)
  }
}

async function postAiRun(
  path: string,
  body: AiTaskRequest,
  onMeta?: (meta: { conversationId: number }) => void
) {
  let out = ''
  await postAiStreamText(
    path,
    body,
    (s) => {
      out += s
    },
    onMeta
  )
  return { output: out }
}

export const aiApi = {
  projectEvaluation: (
    input: string,
    conversationId?: number | null,
    onMeta?: (meta: { conversationId: number }) => void
  ) => postAiRun('/api/ai/project-evaluation', { input, conversationId }, onMeta),
  formulaCompatibility: (
    input: string,
    conversationId?: number | null,
    onMeta?: (meta: { conversationId: number }) => void
  ) => postAiRun('/api/ai/formula-compatibility', { input, conversationId }, onMeta),
  mechanismInference: (
    input: string,
    conversationId?: number | null,
    onMeta?: (meta: { conversationId: number }) => void
  ) => postAiRun('/api/ai/mechanism-inference', { input, conversationId }, onMeta),
  targetPrediction: (
    input: string,
    conversationId?: number | null,
    onMeta?: (meta: { conversationId: number }) => void
  ) => postAiRun('/api/ai/target-prediction', { input, conversationId }, onMeta),
  literatureAnalysis: (
    input: string,
    conversationId?: number | null,
    onMeta?: (meta: { conversationId: number }) => void
  ) => postAiRun('/api/ai/literature-analysis', { input, conversationId }, onMeta),
  patentAnalysis: (
    input: string,
    conversationId?: number | null,
    onMeta?: (meta: { conversationId: number }) => void
  ) => postAiRun('/api/ai/patent-analysis', { input, conversationId }, onMeta),
  experimentDesign: (
    input: string,
    conversationId?: number | null,
    onMeta?: (meta: { conversationId: number }) => void
  ) => postAiRun('/api/ai/experiment-design', { input, conversationId }, onMeta),
  reportGeneration: (
    input: string,
    conversationId?: number | null,
    onMeta?: (meta: { conversationId: number }) => void
  ) => postAiRun('/api/ai/report-generation', { input, conversationId }, onMeta)
}

export async function getAiConversationHistory(agentType: string, limit = 100) {
  const qs = `?agentType=${encodeURIComponent(agentType)}&limit=${encodeURIComponent(String(limit))}`
  return apiGet<AiConversationHistoryItem[]>(`/api/ai/history${qs}`)
}

export async function getAiConversationMessages(conversationId: number) {
  return apiGet<AiAgentMessageItem[]>(`/api/ai/conversations/${conversationId}/messages`)
}

export async function setAiConversationTop(conversationId: number, isTop: boolean) {
  return apiPutJson<void, { isTop: boolean }>(`/api/ai/conversations/${conversationId}/top`, { isTop })
}

export async function deleteAiConversation(conversationId: number) {
  return apiDelete<void>(`/api/ai/conversations/${conversationId}`)
}
