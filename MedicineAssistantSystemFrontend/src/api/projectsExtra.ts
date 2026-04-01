import { apiGet, apiPostJson } from './http'

export type ProjectBoard = {
  projectId: number
  currentPhase: string | null
  status: number | null
  progressPercent: number | null
}

export type ProjectDecision = {
  id?: number
  projectId?: number
  decisionType?: string | null
  title?: string | null
  content?: string | null
  aiRecommendation?: string | null
  expertConclusion?: string | null
  projectorId?: number | null
  version?: number | null
  createTime?: string | null
  updateTime?: string | null
}

export type DecisionCompare = {
  first: ProjectDecision
  second: ProjectDecision
}

export type ProjectDocument = {
  id: number
  projectId: number
  docType: string | null
  docName: string | null
  storageKey: string | null
  originalFilename: string | null
  fileSize: number | null
  fileType: string | null
  uploadUserId: number | null
  uploadTime: string | null
  tags: string | null
  summary: string | null
}

export type ProjectDocumentUpload = {
  uploadUserId?: number | null
  docType?: string | null
  summary?: string | null
}

export type ProjectMember = {
  id: number
  projectId: number
  userId: number
  role: string | null
  joinTime: string | null
}

export type MemberCreate = {
  userId: number
  role: string
  joinTime?: string | null
}

export type DraftEvaluateReq = {
  projectName?: string | null
  herbName?: string | null
  formulaName?: string | null
  indication?: string | null
  description?: string | null
  budget?: number | null
  priority?: string | null
}

export type AiTaskReq = { input: string }

export const projectsExtraApi = {
  board: (projectId: number) => apiGet<ProjectBoard>(`/api/projects/${projectId}/board`),
  decisions: (projectId: number) =>
    apiGet<ProjectDecision[]>(`/api/projects/${projectId}/decisions`),
  compareDecisions: (projectId: number, id1: number, id2: number) =>
    apiGet<DecisionCompare>(
      `/api/projects/${projectId}/decisions/compare?id1=${encodeURIComponent(String(id1))}&id2=${encodeURIComponent(String(id2))}`
    ),
  createDecision: (projectId: number, body: ProjectDecision) =>
    apiPostJson<void, ProjectDecision>(`/api/projects/${projectId}/decisions/create`, body),

  documents: (projectId: number) =>
    apiGet<ProjectDocument[]>(`/api/projects/${projectId}/documents`),
  searchDocuments: (projectId: number, q: string) =>
    apiGet<ProjectDocument[]>(
      `/api/projects/${projectId}/documents/search?q=${encodeURIComponent(q)}`
    ),
  members: (projectId: number) =>
    apiGet<ProjectMember[]>(`/api/projects/${projectId}/members`),
  createMember: (projectId: number, body: MemberCreate) =>
    apiPostJson<void, MemberCreate>(`/api/projects/${projectId}/members/create`, body),

  draftEvaluateStreamUrl: () => `/api/projects/draft/evaluate-stream`,
  reportStreamUrl: (projectId: number) => `/api/projects/${projectId}/ai/report-stream`,

  postDraftEvaluateStream: async (body: DraftEvaluateReq) => {
    const res = await fetch(projectsExtraApi.draftEvaluateStreamUrl(), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })
    if (!res.ok) throw new Error('request failed')
    return res
  },

  postReportStream: async (projectId: number, body?: AiTaskReq) => {
    const res = await fetch(projectsExtraApi.reportStreamUrl(projectId), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body || {})
    })
    if (!res.ok) throw new Error('request failed')
    return res
  }
}
