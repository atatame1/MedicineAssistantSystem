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

export async function listProjects(status?: number) {
  const qs = status != null ? `?status=${encodeURIComponent(String(status))}` : ''
  return apiGet<Project[]>(`/api/projects${qs}`)
}

export async function createProject(body: Project) {
  return apiPostJson<void, Project>('/api/projects/create', body)
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

