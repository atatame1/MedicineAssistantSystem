import { apiGet, apiPostJson } from './http'

export type Project = {
  id?: number
  projectName: string
  herbName: string | null
  formulaName: string | null
  indication: string | null
  phase: string | null
  projectorId: number | null
  status: number | null
  description: string | null
  budget: number | null
  priority: number | null
  aiAssess: string
  aiReport?: string | null
}

export type MyProjectItem = Project & {
  projectorName?: string | null
  currentUserRole?: string | null
}

export async function listProjects(status?: number) {
  const qs = status != null ? `?status=${encodeURIComponent(String(status))}` : ''
  return apiGet<Project[]>(`/api/projects${qs}`)
}

export async function listMyProjects(userId: number, status?: number) {
  const p = new URLSearchParams()
  p.set('userId', String(userId))
  if (status != null) p.set('status', String(status))
  return apiGet<MyProjectItem[]>(`/api/projects/mine?${p.toString()}`)
}

export async function getProjectDetail(projectId: number) {
  return apiGet<Project>(`/api/projects/${projectId}`)
}

export async function createProject(body: Project) {
  return apiPostJson<Project, Project>('/api/projects/create', body)
}

export async function updateProjectStatus(projectId: number, status: number) {
  return apiPostJson<void, { status: number }>(`/api/projects/${projectId}/status`, { status })
}

export async function updateProjectPhase(projectId: number, phase: string) {
  return apiPostJson<void, { phase: string }>(`/api/projects/${projectId}/phase`, { phase })
}

export type ProjectDocument = {
  id: number
  projectId: number
  docType: string
  docName: string
  storageKey: string
  fileSize: number | null
  fileType: string | null
  uploadTime: string | null
  tags: string | null
  summary: string | null
}

export async function listProjectDocuments(projectId: number) {
  return apiGet<ProjectDocument[]>(
    `/api/projects/${projectId}/documents`
  )
}

