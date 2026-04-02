import { apiGet } from './http'

export type AiTaskRequest = { input: string; conversationId?: number | null }
export type AiTaskResponse = { agentCode?: string | null; output: string }

export type AiConversationHistoryItem = {
  type: string | null
  conversationId: number | null
  title: string | null
  inputText: string | null
  outputText: string | null
  createTime: string | null
}

function buildHeaders() {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  const t = localStorage.getItem('mas_token')
  if (t) headers.Authorization = `Bearer ${t}`
  return headers
}

function parseSseDataChunk(buffer: string, onData: (s: string) => void) {
  const parts = buffer.split('\n\n')
  const last = parts.pop() || ''
  for (const p of parts) {
    const lines = p.split('\n')
    for (const line of lines) {
      if (line.startsWith('data:')) {
        let v = line.slice(5)
        if (v.startsWith(' ')) v = v.slice(1)
        if (v !== '') onData(v)
      }
    }
  }
  return last
}

export async function postAiStreamText(
  path: string,
  body: AiTaskRequest,
  onData: (s: string) => void
) {
  const res = await fetch(path, {
    method: 'POST',
    headers: buildHeaders(),
    body: JSON.stringify(body)
  })
  if (!res.ok) throw new Error('request failed')
  const reader = res.body?.getReader()
  if (!reader) throw new Error('no stream')
  const dec = new TextDecoder('utf-8')
  let buffer = ''
  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    buffer += dec.decode(value, { stream: true })
    buffer = parseSseDataChunk(buffer, onData)
  }
}

async function postAiRun(path: string, body: AiTaskRequest) {
  let out = ''
  await postAiStreamText(path, body, (s) => {
    out += s
  })
  return { output: out }
}

export const aiApi = {
  projectEvaluation: (input: string, conversationId?: number | null) =>
    postAiRun('/api/ai/project-evaluation', { input, conversationId }),
  formulaCompatibility: (input: string, conversationId?: number | null) =>
    postAiRun('/api/ai/formula-compatibility', { input, conversationId }),
  mechanismInference: (input: string, conversationId?: number | null) =>
    postAiRun('/api/ai/mechanism-inference', { input, conversationId }),
  targetPrediction: (input: string, conversationId?: number | null) =>
    postAiRun('/api/ai/target-prediction', { input, conversationId }),
  literatureAnalysis: (input: string, conversationId?: number | null) =>
    postAiRun('/api/ai/literature-analysis', { input, conversationId }),
  patentAnalysis: (input: string, conversationId?: number | null) =>
    postAiRun('/api/ai/patent-analysis', { input, conversationId }),
  experimentDesign: (input: string, conversationId?: number | null) =>
    postAiRun('/api/ai/experiment-design', { input, conversationId }),
  reportGeneration: (input: string, conversationId?: number | null) =>
    postAiRun('/api/ai/report-generation', { input, conversationId })
}

export async function getAiConversationHistory(
  agentType: string,
  limit = 100
) {
  const qs = `?agentType=${encodeURIComponent(agentType)}&limit=${encodeURIComponent(String(limit))}`
  return apiGet<AiConversationHistoryItem[]>(`/api/ai/history${qs}`)
}
