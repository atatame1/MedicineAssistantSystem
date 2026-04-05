import { apiGet, apiPostJson } from './http'
import type { Project } from './projects'

export type PortalTask = {
  id: number
  title: string
  description: string | null
  priority: number | null
  status: number | null
  projectId: number | null
  projectName: string | null
  assigneeId: number | null
  deadline: string | null
  overdue: boolean | null
}

export type PortalOverview = {
  tasks: PortalTask[]
  myProjects: Project[]
  riskWarnings: number
  summaryText?: string | null
}

export async function getPortalOverview(userId: number) {
  return apiGet<PortalOverview>(`/api/portal/overview?userId=${encodeURIComponent(String(userId))}`)
}

export async function triggerPortalSummary(userId: number) {
  return apiPostJson<string | null, Record<string, never>>(
    `/api/portal/summarize?userId=${encodeURIComponent(String(userId))}`,
    {}
  )
}
